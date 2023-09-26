package com.franzoia.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.franzoia.common.util.Dto;
import lombok.*;

import java.time.LocalDate;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TransactionDTO(Long id, LocalDate date, TransactionType type,
                             Long productId, ProductDTO product, Double price, Long quantity) implements Dto {

}
