package com.franzoia.productservice.config;

import com.franzoia.common.exception.EntityNotFoundException;
import com.franzoia.common.exception.InvalidRequestException;
import com.franzoia.common.exception.ServiceNotAvailableException;
import feign.Response;
import feign.codec.ErrorDecoder;

public class CategoryErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        return switch (response.status()) {
            case 400 -> new InvalidRequestException("Invalid Category data");
            case 404 -> new EntityNotFoundException("Category not found");
            case 503, 500 -> new ServiceNotAvailableException("Category API is unavailable");
            default -> new Exception("Exception while getting Category details");
        };
    }
}
