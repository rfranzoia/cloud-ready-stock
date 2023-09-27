package com.franzoia.stockservice.service;

import com.franzoia.common.dto.ProductDTO;
import com.franzoia.common.dto.TransactionDTO;
import com.franzoia.common.dto.TransactionType;
import com.franzoia.common.exception.ConstraintsViolationException;
import com.franzoia.common.exception.EntityNotFoundException;
import com.franzoia.common.exception.InvalidRequestException;
import com.franzoia.common.exception.ServiceNotAvailableException;
import com.franzoia.common.util.DefaultService;
import com.franzoia.stockservice.model.Transaction;
import com.franzoia.stockservice.repository.TransactionRepository;
import com.franzoia.stockservice.service.mapper.TransactionMapper;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;


/**
 * Service to encapsulate the link between DAO and controller and to have
 * business logic for some transaction specific things.
 * <p/>
 */
@Slf4j
@Service
public class TransactionService extends DefaultService<TransactionDTO, Transaction, Long, TransactionMapper> {

	@Autowired
	private StockService stockService;

	@Autowired
	private ProductService productService;

	public TransactionService(final TransactionRepository transactionRepository) {
		super(transactionRepository, new TransactionMapper());
	}

	public List<TransactionDTO> listAllOrderByDate() {
		return createTransactionList(findAll());
	}

	public Map<TransactionType, List<TransactionDTO>> listByTpe(final TransactionType type) {
		return createTransactionList(((TransactionRepository) repository).findAllByTypeOrderByDate(type))
				.stream()
				.collect(groupingBy(TransactionDTO::type));
	}

	public List<TransactionDTO> listByDates(final LocalDate startDate, final LocalDate endDate) {
		return createTransactionList(((TransactionRepository) repository).findAllByDateBetweenOrderByDate(startDate, endDate));
	}

	public List<TransactionDTO> listByDatesAndProduct(final LocalDate startDate, final LocalDate endDate, final Long productId) throws EntityNotFoundException, ServiceNotAvailableException {
		ProductDTO product = productService.getProductById(productId);
		return createTransactionList(((TransactionRepository) repository).findAllByDateBetweenAndProductIdOrderByDate(startDate, endDate, productId), product);
	}

	@Transactional
	public TransactionDTO create(TransactionDTO dto) throws ConstraintsViolationException, EntityNotFoundException, InvalidRequestException, ServiceNotAvailableException {
		// implicit product validation
		ProductDTO product = productService.getProductById(dto.productId());

		// additional validation
		if (dto.date().isAfter(LocalDate.now())) {
			throw new InvalidRequestException("Future transactions are not permitted");

		} else if (dto.date().isBefore(LocalDate.of(LocalDate.now().getYear(), 1, 1))) {
			throw new InvalidRequestException("Only current year transactions are permitted");

		} else if (dto.quantity() < 0 || dto.price() < 0) {
			throw new InvalidRequestException("Transactions cannot have quantity or price bellow zero");
		}

		// create the transaction and update the stock information
		Transaction transaction = create(mapper.convertDtoToEntity(dto));
		switch (dto.type()) {
			case INPUT -> stockService.addToStock(dto.date(), dto.productId(), dto.quantity());
			case OUTPUT -> stockService.removeFromStock(dto.date(), dto.productId(), dto.quantity());
			default -> throw new InvalidRequestException("couldn't create the transaction and update stock");
		}
		stockService.syncStockBalance(dto.productId());

		return TransactionDTO.builder()
				.id(transaction.getId())
				.type(dto.type())
				.date(transaction.getDate())
				.product(product)
				.price(transaction.getPrice())
				.quantity(transaction.getQuantity())
				.build();
	}

	@Override
	@Transactional
	public void delete(Long transactionId) throws EntityNotFoundException, InvalidRequestException {
		// check if the transaction exists
		Transaction transaction = findByIdChecked(transactionId);

		// update stock information before deleting the transaction
		switch (transaction.getType()) {
			case INPUT -> stockService.removeFromStock(transaction.getDate(), transaction.getProductId(), transaction.getQuantity());
			case OUTPUT -> stockService.addToStock(transaction.getDate(), transaction.getProductId(), transaction.getQuantity());
			default -> throw new InvalidRequestException("Could not update stock information for transaction");
		}

		// delete the transaction
		super.delete(transactionId);
	}

	private List<TransactionDTO> createTransactionList(final List<Transaction> transactions) {
		return createTransactionList(transactions, null);
	}

	private List<TransactionDTO> createTransactionList(final List<Transaction> transactions, final ProductDTO product) {
		Map<Long, List<ProductDTO>> productsMap = product == null? productService.getProductMap() : null;
		List<TransactionDTO> list = new ArrayList<>();
		transactions.forEach(t -> {
			TransactionDTO dto = TransactionDTO.builder()
					.id(t.getId())
					.date(t.getDate())
					.type(t.getType())
					.product(product != null? product: productsMap.get(t.getProductId()).get(0))
					.price(t.getPrice())
					.quantity(t.getQuantity())
					.build();
			list.add(dto);
		});
		return list.stream().sorted(Comparator.comparing(t -> t.date().format(DateTimeFormatter.ofPattern("yyyyMMdd")))).toList();
	}

	public ValidDates getValidDates(String startDate, String endDate) {
		LocalDate start, end;

		if (startDate == null) {
			start = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), 1);
		} else {
			start = LocalDate.parse(startDate);
		}
		if (endDate == null) {
			end = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), LocalDate.now().lengthOfMonth());
		} else {
			end = LocalDate.parse(endDate);
		}
		if (end.isBefore(start)) {
			throw new InvalidRequestException("End date cannot be before start date");
		}
		return ValidDates.builder()
				.start(start)
				.end(end)
				.build();
	}

	@Builder
	public record ValidDates(LocalDate start, LocalDate end) {}
}
