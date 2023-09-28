package com.franzoia.transactionservice.config;

import com.franzoia.common.dto.StockUpdateRequest;
import com.franzoia.common.exception.EntityNotFoundException;
import com.franzoia.common.exception.InvalidRequestException;
import com.franzoia.common.exception.ServiceNotAvailableException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "STOCK-SERVICE", path = "/stock-service/api/v1/stocks",
        configuration = { CustomErrorDecoder.class })
public interface StockFeignClient {

    @PutMapping("/yearMonth/{yearMonth}/product/{productId}")
    void updateStock(@PathVariable("yearMonth") final String yearMonth,
                     @PathVariable("productId") final Long productId, @RequestBody final StockUpdateRequest updateRequest)
            throws EntityNotFoundException, InvalidRequestException, ServiceNotAvailableException;

}
