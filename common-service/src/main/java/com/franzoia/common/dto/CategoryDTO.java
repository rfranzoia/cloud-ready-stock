package com.franzoia.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.franzoia.common.util.Dto;
import lombok.*;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CategoryDTO(Long id, String name) implements Dto {

}
