package com.franzoia.categoryservice.repository;

import com.franzoia.categoryservice.model.Category;

import reactor.core.publisher.Flux;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;


@Repository
public interface CategoryRepository extends ReactiveCrudRepository<Category, Long> {

    Flux<Category> findAllByNameLikeOrderByName(final String name);

    Mono<Category> findByName(final String name);

}
