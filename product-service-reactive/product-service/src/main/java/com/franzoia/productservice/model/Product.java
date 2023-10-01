package com.franzoia.productservice.model;

import com.franzoia.common.util.reactive.ReactiveAuditableEntity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@Data
@Table(value = "products")
public class Product implements ReactiveAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NotNull
    private String name;

    @Column
    @NotNull
    private Long categoryId;

    @Column
    private String unit;

    @Column
    private Double price;

    @Column
    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime dateCreated = ZonedDateTime.now();

    @Column
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime dateUpdated = ZonedDateTime.now();

    public Product(final Long id, @NotNull final String name, @NotNull final Long categoryId, final String unit, final Double price) {
        this.id = id;
        this.name = name;
        this.categoryId = categoryId;
        this.unit = unit;
        this.price = price;
    }

}
