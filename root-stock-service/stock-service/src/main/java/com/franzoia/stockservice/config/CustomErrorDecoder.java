package com.franzoia.stockservice.config;

import com.franzoia.common.exception.EntityNotFoundException;
import com.franzoia.common.exception.InvalidRequestException;
import com.franzoia.common.exception.ServiceNotAvailableException;
import feign.Response;
import feign.codec.ErrorDecoder;

public class CustomErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        return switch (response.status()) {
            case 400 -> new InvalidRequestException("Invalid parameters");
            case 404 -> new EntityNotFoundException("Entity not found");
            case 503 -> new ServiceNotAvailableException("Entity Api is unavailable");
            default -> new Exception("Exception while getting entity details");
        };
    }
}
