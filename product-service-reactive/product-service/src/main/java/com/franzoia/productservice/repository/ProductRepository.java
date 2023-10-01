package com.franzoia.productservice.repository;

import com.franzoia.productservice.model.Product;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductRepository extends ReactiveCrudRepository<Product, Long> {

    Flux<Product> findAllByNameLikeOrderByName(final String name);

    Mono<Product> findByName(final String name);

    Flux<Product> findAllByCategoryIdOrderByName(final Long categoryId);

}
