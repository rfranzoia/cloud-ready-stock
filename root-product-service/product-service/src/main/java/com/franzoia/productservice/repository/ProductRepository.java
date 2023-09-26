package com.franzoia.productservice.repository;

import com.franzoia.productservice.model.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {

    List<Product> findAllByNameLikeOrderByName(final String name);

    List<Product> findAllByCategoryIdOrderByName(final Long categoryId);

}
