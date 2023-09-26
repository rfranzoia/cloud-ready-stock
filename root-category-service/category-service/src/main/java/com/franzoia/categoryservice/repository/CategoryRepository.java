package com.franzoia.categoryservice.repository;

import com.franzoia.categoryservice.model.Category;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Long> {

    List<Category> findAllByNameLikeOrderByName(final String name);

}
