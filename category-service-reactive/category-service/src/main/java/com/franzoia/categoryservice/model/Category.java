package com.franzoia.categoryservice.model;


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
@Table(value = "categories")
public class Category implements ReactiveAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NotNull
    private String name;

    @Column
    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime dateCreated = ZonedDateTime.now();

    @Column
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime dateUpdated = ZonedDateTime.now();

    public Category(Long id, String name) {
        this.id = id;
        this.name = name;
    }

}
