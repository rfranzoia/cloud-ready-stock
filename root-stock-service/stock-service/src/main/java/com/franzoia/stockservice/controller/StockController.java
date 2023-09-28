package com.franzoia.stockservice.controller;

import com.franzoia.common.dto.StockDTO;
import com.franzoia.common.dto.StockUpdateRequest;
import com.franzoia.common.exception.EntityNotFoundException;
import com.franzoia.common.exception.InvalidRequestException;
import com.franzoia.common.exception.ServiceNotAvailableException;
import com.franzoia.stockservice.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stocks")
public class StockController {

    private final StockService stockService;

    @Autowired
    public StockController(StockService service) {
        this.stockService = service;
    }

    @GetMapping
    public List<StockDTO> findAllTransaction() throws ServiceNotAvailableException, EntityNotFoundException {
        return stockService.listALl();
    }

    @GetMapping("/yearMonth/{yearMonth}/product/{productId}")
    public StockDTO getByYearMonthAndProduct(@PathVariable("yearMonth") final String yearMonth,
                                             @PathVariable("productId") final Long productId)
            throws EntityNotFoundException, ServiceNotAvailableException {
        return stockService.getByYearMonthAndProduct(yearMonth, productId);
    }

    @GetMapping("/product/{productId}")
    public List<StockDTO> listByProduct(@PathVariable("productId") final Long productId)
            throws EntityNotFoundException, ServiceNotAvailableException {
        return stockService.listByProduct(productId);
    }

    @GetMapping("/yearMonth/{yearMonth}")
    public List<StockDTO> listByYearMonth(@PathVariable("yearMonth") final String yearMonth)
            throws ServiceNotAvailableException, EntityNotFoundException {
        return stockService.listByYearMonth(yearMonth);
    }

    @PutMapping("/yearMonth/{yearMonth}/product/{productId}")
    void updateStock(@PathVariable("yearMonth") final String yearMonth,
                     @PathVariable("productId") final Long productId, @RequestBody final StockUpdateRequest updateRequest)
            throws EntityNotFoundException, InvalidRequestException, ServiceNotAvailableException {
        stockService.updateStock(yearMonth, productId, updateRequest);
    }
}
