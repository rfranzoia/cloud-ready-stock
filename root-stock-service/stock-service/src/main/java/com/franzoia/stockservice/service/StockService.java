package com.franzoia.stockservice.service;

import com.franzoia.common.dto.ProductDTO;
import com.franzoia.common.dto.StockDTO;
import com.franzoia.common.dto.StockKey;
import com.franzoia.common.exception.EntityNotFoundException;
import com.franzoia.common.exception.InvalidRequestException;
import com.franzoia.common.exception.ServiceNotAvailableException;
import com.franzoia.common.util.DefaultService;
import com.franzoia.stockservice.model.Stock;
import com.franzoia.stockservice.repository.StockRepository;
import com.franzoia.stockservice.service.mapper.StockMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Slf4j
@Service
public class StockService extends DefaultService<StockDTO, Stock, StockKey, StockMapper> {

    @Autowired
    private ProductService productService;

    public StockService(final StockRepository stockRepository) {
        super(stockRepository, new StockMapper());
    }

    /**
     * List all stock information available
     * @return List of stock data
     */
    public List<StockDTO> listALl() throws ServiceNotAvailableException, EntityNotFoundException {
        final Map<Long, List<ProductDTO>> productsMap = productService.getProductMap();
        return createListOfStockDTO(((StockRepository) repository).findAllOrderByYearMonthPeriodAndProductId(), null);
    }

    /**
     * Retrieve the Stock information for a specific Product and Yeah/Month period
     *
     * @param period the year/month perior
     * @param productId the id of the product
     * @return the stock information
     * @throws EntityNotFoundException when the product is not found
     * @throws ServiceNotAvailableException the the product-service is not available
     */
    public StockDTO getByYearMonthPeriodAndProduct(final String period, final Long productId) throws EntityNotFoundException, ServiceNotAvailableException {
        final ProductDTO product = productService.getProductById(productId);
        final Stock stock = findByIdChecked(new StockKey(period, productId));
        return StockDTO.builder()
                .key(stock.getKey())
                .product(product)
                .inputs(stock.getInputs())
                .outputs(stock.getOutputs())
                .previousBalance(stock.getPreviousBalance())
                .currentBalance(stock.getCurrentBalance())
                .build();
    }

    /**
     * List all Stock information for a specific product
     *
     * @param productId id of the product
     * @return a list of stocks for the product
     * @throws EntityNotFoundException if the product doesn't exists
     * @throws ServiceNotAvailableException if the product service is not available to validate the product
     */
    public List<StockDTO> listByProduct(final Long productId) throws EntityNotFoundException, ServiceNotAvailableException {
        final ProductDTO product = productService.getProductById(productId);
        return createListOfStockDTO(((StockRepository)repository).findAllByProductId(productId), product);
    }

    public List<StockDTO> listByYearMonthPeriod(final String yeahMonthPeriod)
            throws ServiceNotAvailableException, EntityNotFoundException {
        return createListOfStockDTO(((StockRepository) repository).findAllByYearMonthPeriod(yeahMonthPeriod), null);
    }

    private List<StockDTO> createListOfStockDTO(final List<Stock> stocks, final ProductDTO product)
            throws EntityNotFoundException, ServiceNotAvailableException {
        final Map<Long, List<ProductDTO>> productsMap = product == null? productService.getProductMap(): null;
        return stocks.stream()
                        .map(s -> StockDTO.builder()
                                .key(s.getKey())
                                .product(product == null? productsMap.get(s.getKey().getProductId()).get(0): product)
                                .inputs(s.getInputs())
                                .outputs(s.getOutputs())
                                .previousBalance(s.getPreviousBalance())
                                .currentBalance(s.getCurrentBalance())
                                .build()).toList();
    }

    /**
     * Add a quantity to the stock of a product on a specific date, converted to Year/Month
     */
    @Transactional
    public void addToStock(final LocalDate date, final Long productId, final Long quantity) throws EntityNotFoundException {
        // convert received date to YearMonth period string
        String yearMonth = date.format(DateTimeFormatter.ofPattern("yyyyMM"));

        // retrieve the Stock for the Product
        Stock stock = repository.findById(new StockKey(yearMonth, productId)).orElse(null);

        if (stock == null) {
            Stock previousStock = getPreviousMonthStock(date, productId);
            Long previous = previousStock == null? 0L: previousStock.getCurrentBalance();
            saveStockUpdate(yearMonth, productId,
                    quantity,
                    0L,
                    previous,
                    previous + quantity);
        } else {
            saveStockUpdate(yearMonth, productId,
                    stock.getInputs() + quantity,
                    stock.getOutputs(),
                    stock.getPreviousBalance(),
                    Math.min(stock.getCurrentBalance() + quantity, 0));
        }
        if (!date.getMonth().equals(LocalDate.now().getMonth())) {
            updateForwardStock(date, productId);
        }
    }

    /**
     * removes a quantity from the stock of a product on a specific date, converted to Year/Month
     */
    public void removeFromStock(final LocalDate date, final Long productId, final Long quantity) throws EntityNotFoundException, InvalidRequestException {
        // convert received date to YearMonth period string
        String yearMonth = date.format(DateTimeFormatter.ofPattern("yyyyMM"));

        // retrieve the Stock for the Product
        Stock stock = repository.findById(new StockKey(yearMonth, productId)).orElse(null);

        if (stock == null) {
            Stock previousStock = getPreviousMonthStock(date, productId);
            if (previousStock == null ) {
                throw new EntityNotFoundException("No Stock information found, remove not possible");

            } else if (previousStock.getCurrentBalance() < quantity) {
                throw new InvalidRequestException("Removal quantity cannot exceed the current balance");

            } else {
                saveStockUpdate(yearMonth, productId,
                        0L,
                        quantity,
                        previousStock.getCurrentBalance(),
                        Math.min(previousStock.getCurrentBalance() - quantity, 0));

            }
        } else if (stock.getCurrentBalance() < quantity) {
            throw new InvalidRequestException("Removal quantity cannot exceed the current balance");

        } else {
            saveStockUpdate(yearMonth, productId,
                    stock.getInputs(),
                    stock.getOutputs() + quantity,
                    stock.getPreviousBalance(),
                    Math.min(stock.getCurrentBalance() - quantity, 0));

        }
        if (!date.getMonth().equals(LocalDate.now().getMonth())) {
            updateForwardStock(date, productId);
        }
    }

    /**
     * this will update all stock information for a product
     *
     * @param productId id of the product
     * @throws EntityNotFoundException if the product doesn't exists
     * @throws ServiceNotAvailableException if the product service is not available to validate the product
     */
    @Transactional
    public void syncStockBalance(final Long productId) throws EntityNotFoundException, ServiceNotAvailableException {
        List<StockDTO> list = listByProduct(productId);
        Long previousBalance = 0L;
        for (StockDTO dto : list) {
            StockDTO updatedDTO = StockDTO.builder()
                    .key(dto.getKey())
                    .inputs(dto.getInputs())
                    .outputs(dto.getOutputs())
                    .previousBalance(previousBalance)
                    .currentBalance(previousBalance + dto.getInputs() - dto.getOutputs())
                    .build();
            repository.save(mapper.convertDtoToEntity(updatedDTO));
            previousBalance = updatedDTO.getCurrentBalance();
        }
    }



    private void updateForwardStock(final LocalDate date, final Long productId) {
        String currentPeriod = date.format(DateTimeFormatter.ofPattern("yyyyMM"));
        Stock currentStock = repository.findById(new StockKey(currentPeriod, productId)).orElse(null);

        assert currentStock != null;
        Long balance = currentStock.getCurrentBalance();
        LocalDate nextMonth = date;

        do {
            nextMonth = nextMonth.plusMonths(1);
            String nextPeriod = nextMonth.format(DateTimeFormatter.ofPattern("yyyyMM"));
            Stock nextMoonthStock = repository.findById(new StockKey(nextPeriod, productId)).orElse(null);
            if (nextMoonthStock != null) {
                saveStockUpdate(nextPeriod, productId, nextMoonthStock.getInputs(), nextMoonthStock.getOutputs(),
                        balance, Math.min(balance + nextMoonthStock.getInputs() - nextMoonthStock.getOutputs(), 0));
                balance = nextMoonthStock.getCurrentBalance();
            } else {
                saveStockUpdate(nextPeriod, productId, 0L, 0L, balance, balance);
            }

        } while (!nextMonth.getMonth().equals(LocalDate.now().getMonth()));
    }

    private void saveStockUpdate(String yearMonth, Long productId, Long in, Long out, Long previous, Long current) {
        StockDTO dto = StockDTO.builder()
                .key(new StockKey(yearMonth, productId))
                .inputs(in)
                .outputs(out)
                .previousBalance(previous)
                .currentBalance(current)
                .build();
        log.info(dto.toString());
        Stock entity = mapper.convertDtoToEntity(dto);
        repository.save(entity);
    }

    private String getPreviousMonthPeriod(LocalDate date) {
        LocalDate previousMonth = date.plusMonths(-1);
        return previousMonth.format(DateTimeFormatter.ofPattern("yyyyMM"));
    }

    private Stock getPreviousMonthStock(LocalDate date, Long productId) {
        String yearMonth = getPreviousMonthPeriod(date);
        return repository.findById(new StockKey(yearMonth, productId)).orElse(null);
    }
}
