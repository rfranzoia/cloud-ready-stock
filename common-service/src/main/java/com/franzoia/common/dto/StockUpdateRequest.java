package com.franzoia.common.dto;

import lombok.*;

@Builder
public record StockUpdateRequest(TransactionType type, Long quantity) {}
