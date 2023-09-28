package com.franzoia.common.dto;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Embeddable
public class StockKey implements Serializable {

    private String yearMonth;
    private Long productId;

}
