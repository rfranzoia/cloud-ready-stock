package com.franzoia.stockservice.service;

import com.franzoia.common.dto.*;
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
import java.util.function.Function;

import static java.util.stream.Collectors.groupingBy;

@Slf4j
@Service
public class StockService extends DefaultService<StockDTO, Stock, StockKey, StockMapper> {

    public static final DateTimeFormatter YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyyMMdd");
    public static final DateTimeFormatter YYYY_MM = DateTimeFormatter.ofPattern("yyyyMM");

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
        return createListOfStockDTO(((StockRepository) repository).findAllOrderByYearMonthAndProductId(), null);
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
    public StockDTO getByYearMonthAndProduct(final String period, final Long productId) throws EntityNotFoundException, ServiceNotAvailableException {
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

    public List<StockDTO> listByYearMonth(final String yeahMonthPeriod) {
        return createListOfStockDTO(((StockRepository) repository).findAllByYearMonth(yeahMonthPeriod), null);
    }

    private List<StockDTO> createListOfStockDTO(final List<Stock> stocks, final ProductDTO product) {
        final Map<Long, List<ProductDTO>> productsMap = product == null? productService.getProductMap(): null;

        // fallback for category-service
        final Function<Long, ProductDTO> prod = id -> {
            if (productsMap == null || productsMap.isEmpty() || !productsMap.containsKey(id)) {
                return ProductDTO.builder()
                        .name("Unavailable Product Data")
                        .build();
            } else {
                return productsMap.get(id).get(0);
            }
        };

        return stocks.stream()
                        .map(s -> StockDTO.builder()
                                .key(s.getKey())
                                .product(product == null? prod.apply(s.getKey().getProductId()): product)
                                .inputs(s.getInputs())
                                .outputs(s.getOutputs())
                                .previousBalance(s.getPreviousBalance())
                                .currentBalance(s.getCurrentBalance())
                                .build()).toList();
    }

    @Transactional
    public void addOrUpdateStock(final StockUpdateRequest updateRequest)
            throws EntityNotFoundException, ServiceNotAvailableException {
        switch (updateRequest.type()) {
            case INPUT -> addToStock(updateRequest.key().getYearMonth(), updateRequest.key().getProductId(), updateRequest.quantity());
            case OUTPUT -> removeFromStock(updateRequest.key().getYearMonth(), updateRequest.key().getProductId(), updateRequest.quantity());
        }
        // just make sure everything is correctly calculated
        syncStockBalance(updateRequest.key().getProductId());
    }

    /**
     * Add a quantity to the stock of a product on a specific date, converted to Year/Month
     */
    @Transactional
    public void addToStock(final String yearMonth, final Long productId, final Long quantity) throws EntityNotFoundException {
        // retrieve the Stock for the Product
        Stock stock = repository.findById(new StockKey(yearMonth, productId)).orElse(null);

        if (stock == null) {
            Stock previousStock = getPreviousMonthStock(yearMonth, productId);
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
        final String today = LocalDate.now().format(YYYY_MM);
        if (Integer.parseInt(yearMonth) < Integer.parseInt(today)) {
            updateForwardStock(yearMonth, productId);
        }
    }

    /**
     * removes a quantity from the stock of a product on a specific date, converted to Year/Month
     */
    public void removeFromStock(final String yearMonth, final Long productId, final Long quantity) throws EntityNotFoundException, InvalidRequestException {
        // retrieve the Stock for the Product
        StockKey key = new StockKey(yearMonth, productId);
        Stock stock = repository.findById(key).orElse(null);

        if (stock == null) {
            Stock previousStock = getPreviousMonthStock(yearMonth, productId);
            if (previousStock == null ) {
                log.info("No Stock information found for {}, remove not possible", key);
                throw new EntityNotFoundException("No Stock information found, remove not possible");

            } else if (previousStock.getCurrentBalance() < quantity) {
                log.info("Removal quantity cannot exceed the current balance");
                throw new InvalidRequestException("quantity cannot exceed the current balance");

            } else {
                saveStockUpdate(yearMonth, productId,
                        0L,
                        quantity,
                        previousStock.getCurrentBalance(),
                        Math.min(previousStock.getCurrentBalance() - quantity, 0));

            }
        } else if (stock.getCurrentBalance() < quantity) {
            log.info("Removal quantity cannot exceed the current balance");
            throw new InvalidRequestException("Removal quantity cannot exceed the current balance");

        } else {
            saveStockUpdate(yearMonth, productId,
                    stock.getInputs(),
                    stock.getOutputs() + quantity,
                    stock.getPreviousBalance(),
                    Math.min(stock.getCurrentBalance() - quantity, 0));

        }
        final String today = LocalDate.now().format(YYYY_MM);
        if (Integer.parseInt(yearMonth) < Integer.parseInt(today)) {
            updateForwardStock(yearMonth, productId);
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
            // ignore previous years stock since the listByProduct brings everything
            int stockYear = LocalDate.parse(dto.getKey().getYearMonth() + "01", YYYY_MM_DD).getYear();
            if (stockYear < LocalDate.now().getYear()) continue;

            // update the stock quantities
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



    private void updateForwardStock(final String currentPeriod, final Long productId) {
        Stock currentStock = repository.findById(new StockKey(currentPeriod, productId)).orElse(null);

        assert currentStock != null;
        Long balance = currentStock.getCurrentBalance();
        LocalDate nextMonth = LocalDate.parse(currentPeriod + "01", YYYY_MM_DD);

        do {
            nextMonth = nextMonth.plusMonths(1);
            String nextPeriod = nextMonth.format(YYYY_MM);
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

    private void saveStockUpdate(final String yearMonth, final Long productId, final Long in, final Long out, final Long previous, final Long current) {
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

    private Stock getPreviousMonthStock(final String yearMonth, final Long productId) {
        LocalDate currentDate = LocalDate.parse(yearMonth + "01", YYYY_MM_DD);
        String previousYearMonth = currentDate.plusMonths(-1).format(YYYY_MM);
        return repository.findById(new StockKey(previousYearMonth, productId)).orElse(null);
    }
}
