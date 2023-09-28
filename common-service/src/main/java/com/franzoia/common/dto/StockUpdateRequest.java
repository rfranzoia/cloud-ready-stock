package com.franzoia.common.dto;

import lombok.*;

@Builder
public record StockUpdateRequest(StockKey key, TransactionType type, Long quantity) {}
