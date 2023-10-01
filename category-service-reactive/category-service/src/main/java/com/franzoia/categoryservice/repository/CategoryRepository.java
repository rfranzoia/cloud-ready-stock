package com.franzoia.categoryservice.repository;

import com.franzoia.categoryservice.model.Category;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CategoryRepository extends ReactiveCrudRepository<Category, Long> {

    Flux<Category> findAllByNameLikeOrderByName(final String name);

}
