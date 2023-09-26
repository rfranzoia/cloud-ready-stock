package com.franzoia.stockservice.model;

import com.franzoia.common.dto.StockKey;
import com.franzoia.common.util.DefaultEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "stocks")
public class Stock implements DefaultEntity {

    @Id
    private StockKey key;

    @Column
    private Long previousBalance;

    @Column
    private Long inputs;

    @Column
    private Long outputs;

    @Column
    private Long currentBalance;
}
