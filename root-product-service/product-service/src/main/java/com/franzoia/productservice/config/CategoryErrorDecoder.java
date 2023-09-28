package com.franzoia.productservice.config;

import com.franzoia.common.config.DefaultErrorDecoder;
import com.franzoia.common.exception.EntityNotFoundException;
import com.franzoia.common.exception.InvalidRequestException;
import com.franzoia.common.exception.ServiceNotAvailableException;
import feign.Response;
import feign.codec.ErrorDecoder;

public class CategoryErrorDecoder extends DefaultErrorDecoder {

    protected CategoryErrorDecoder() {
        setServiceName("Category");
    }
}
