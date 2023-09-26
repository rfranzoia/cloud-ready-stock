package com.franzoia.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.franzoia.common.util.Dto;
import lombok.*;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProductDTO(Long id, String name, Long categoryId, CategoryDTO category, String unit, Double price) implements Dto {

}
