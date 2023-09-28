package com.franzoia.transactionservice.service;

import com.franzoia.common.dto.StockUpdateRequest;
import com.franzoia.common.dto.TransactionType;
import com.franzoia.common.exception.EntityNotFoundException;
import com.franzoia.common.exception.ServiceNotAvailableException;
import com.franzoia.transactionservice.config.StockFeignClient;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class StockService {

    public static final DateTimeFormatter YYYY_MM = DateTimeFormatter.ofPattern("yyyyMM");

    @Autowired
    private StockFeignClient stockFeignClient;

    public void addToStock(LocalDate date, Long productId, Long quantity) throws ServiceNotAvailableException, EntityNotFoundException {
        try {
            final String yearMonth = date.format(YYYY_MM);
            stockFeignClient.updateStock(yearMonth, productId,
                    StockUpdateRequest.builder()
                            .type(TransactionType.INPUT)
                            .quantity(quantity)
                            .build());
        } catch (FeignException fe) {
            log.error("Something went wrong with the stock-service", fe);
            throw fe;
        }
    }

    public void removeFromStock(LocalDate date, Long productId, Long quantity) throws ServiceNotAvailableException, EntityNotFoundException {
        try {
            final String yearMonth = date.format(YYYY_MM);
            stockFeignClient.updateStock(yearMonth, productId,
                    StockUpdateRequest.builder()
                            .type(TransactionType.OUTPUT)
                            .quantity(quantity)
                            .build());
        } catch (FeignException fe) {
            log.error("Something went wrong with the stock-service", fe);
            throw fe;
        }
    }

}
