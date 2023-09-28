package com.franzoia.transactionservice.config;

import com.franzoia.common.dto.StockUpdateRequest;
import com.franzoia.common.exception.EntityNotFoundException;
import com.franzoia.common.exception.InvalidRequestException;
import com.franzoia.common.exception.ServiceNotAvailableException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "STOCK-SERVICE", path = "/stock-service/api/v1/stocks",
        configuration = { StockErrorDecoder.class })
public interface StockFeignClient {

    @PostMapping
    void addOrRemoveStock(@RequestBody final StockUpdateRequest updateRequest)
            throws EntityNotFoundException, InvalidRequestException, ServiceNotAvailableException;

}
