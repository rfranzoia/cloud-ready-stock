package com.franzoia.transactionservice.config;

import com.franzoia.common.config.DefaultErrorDecoder;

public class StockErrorDecoder extends DefaultErrorDecoder {

    protected StockErrorDecoder() {
        setServiceName("Stock");
    }
}
