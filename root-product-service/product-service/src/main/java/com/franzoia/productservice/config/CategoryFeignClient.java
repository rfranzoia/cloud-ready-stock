package com.franzoia.productservice.config;

import com.franzoia.common.dto.CategoryDTO;
import com.franzoia.common.exception.EntityNotFoundException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "CATEGORY-SERVICE", path = "/category-service/api/v1/categories")
public interface CategoryFeignClient {

    @GetMapping
    List<CategoryDTO> listCategories();

    @GetMapping("/{categoryId}")
    CategoryDTO getByCategoryId(@PathVariable("categoryId") final Long categoryId) throws EntityNotFoundException;

    @GetMapping("/name/{name}")
    List<CategoryDTO> listCategoriesByName(@PathVariable("name") final String name);

}

