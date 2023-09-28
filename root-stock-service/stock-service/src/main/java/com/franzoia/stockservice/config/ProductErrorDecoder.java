package com.franzoia.stockservice.config;

import com.franzoia.common.config.DefaultErrorDecoder;

public class ProductErrorDecoder extends DefaultErrorDecoder {

    protected ProductErrorDecoder() {
        setServiceName("Product");
    }
}
