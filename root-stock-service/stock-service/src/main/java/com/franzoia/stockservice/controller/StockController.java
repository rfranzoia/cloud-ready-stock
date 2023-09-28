package com.franzoia.stockservice.controller;

import com.franzoia.common.dto.StockDTO;
import com.franzoia.common.dto.StockUpdateRequest;
import com.franzoia.common.exception.EntityNotFoundException;
import com.franzoia.common.exception.InvalidRequestException;
import com.franzoia.common.exception.ServiceNotAvailableException;
import com.franzoia.stockservice.service.StockService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.*;

@Tag(name = "Stock", description = "Stock management API")
@RestController
@RequestMapping("/api/v1/stocks")
public class StockController {

    private final StockService stockService;

    @Autowired
    public StockController(StockService service) {
        this.stockService = service;
    }

    @Operation(
            summary = "Retrieves all Stock data available",
            description = "Check all available stock information and creates a List<StockDTO>",
            tags = { "Stock" })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "A List of all Stock information"),
            @ApiResponse(responseCode = "503", description = "When the product-service is not available", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "500", description = "Error with the server", content = { @Content(schema = @Schema()) }) })
    @GetMapping
    public List<StockDTO> findAllTransaction() throws ServiceNotAvailableException, EntityNotFoundException {
        return stockService.listALl();
    }

    @Operation(
            summary = "Retrieve one Stock for Year/Month and Product",
            description = "Brings information about the stock of one product at the Year/Month provided",
            tags = { "Stock" })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The Stock information"),
            @ApiResponse(responseCode = "404", description = "When there's not stock for the Year/Month/Product provided or simply when the Product doesn't exists",
                    content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "503", description = "When the product-service is not available", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "500", description = "Error with the server", content = { @Content(schema = @Schema()) }) })
    @GetMapping("/yearMonth/{yearMonth}/product/{productId}")
    public StockDTO getByYearMonthAndProduct(@PathVariable("yearMonth") final String yearMonth,
                                             @PathVariable("productId") final Long productId)
            throws EntityNotFoundException, ServiceNotAvailableException {
        return stockService.getByYearMonthAndProduct(yearMonth, productId);
    }

    @Operation(
            summary = "Retrieves all Stock data available for a product",
            description = "Check all available stock information for a provided product and creates a List<StockDTO>",
            tags = { "Stock" })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "A List of all Stock information for a single product"),
            @ApiResponse(responseCode = "404", description = "When the provided product doesn't exists", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "503", description = "When the product-service is not available", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "500", description = "Error with the server", content = { @Content(schema = @Schema()) }) })
    @GetMapping("/product/{productId}")
    public List<StockDTO> listByProduct(@PathVariable("productId") final Long productId)
            throws EntityNotFoundException, ServiceNotAvailableException {
        return stockService.listByProduct(productId);
    }

    @Operation(
            summary = "Retrieves all Stock from all products for an Year/Month",
            description = "Check all available stock information from all products for a provided year/month and creates a List<StockDTO>",
            tags = { "Stock" })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "A List of all Stock information for a Year/Month"),
            @ApiResponse(responseCode = "503", description = "When the product-service is not available", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "500", description = "Error with the server", content = { @Content(schema = @Schema()) }) })
    @GetMapping("/yearMonth/{yearMonth}")
    public List<StockDTO> listByYearMonth(@PathVariable("yearMonth") final String yearMonth) {
        return stockService.listByYearMonth(yearMonth);
    }

    @Operation(
            summary = "Update stock information",
            description = "Add or Remove stock information for a product/year/month based on the provided data",
            tags = { "Stock" })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The information has been updated for the stock"),
            @ApiResponse(responseCode = "400", description = "When the quantify provided is invalid", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "404", description = "When the provided Product doesn't exist", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "503", description = "When the product-service is not available", content = { @Content(schema = @Schema()) }),
            @ApiResponse(responseCode = "500", description = "Error with the server", content = { @Content(schema = @Schema()) }) })
    @PostMapping
    void addOrUpdateStock(@RequestBody final StockUpdateRequest updateRequest)
            throws EntityNotFoundException, InvalidRequestException, ServiceNotAvailableException {
        stockService.addOrUpdateStock(updateRequest);
    }
}
