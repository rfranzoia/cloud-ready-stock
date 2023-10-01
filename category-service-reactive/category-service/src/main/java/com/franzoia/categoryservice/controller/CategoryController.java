package com.franzoia.categoryservice.controller;

import java.util.Comparator;

import com.franzoia.categoryservice.service.CategoryReactiveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.franzoia.common.dto.CategoryDTO;
import com.franzoia.common.util.ErrorResponse;

import io.swagger.v3.oas.annotations.Operation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * All operations with a category will be routed by this controller.
 * <p/>
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

	private final CategoryReactiveService categoriesService;

	@Autowired
	public CategoryController(final CategoryReactiveService categorysService) {
		this.categoriesService = categorysService;
	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "Find All Categories")
	public Flux<CategoryDTO> listAll() {
		return categoriesService.findAll()
				.map(c -> categoriesService.getMapper().convertEntityToDTO(c))
				.sort(Comparator.comparing(CategoryDTO::name));
	}

	@GetMapping("/{categoryId}")
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "Find ONE category by ID")
	public Mono<CategoryDTO> getCategory(@PathVariable final long categoryId) {
		return categoriesService.findOne(categoryId)
					.map(c -> categoriesService.getMapper().convertEntityToDTO(c));
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Creates a new Category")
	public Mono<CategoryDTO> createCategory(@RequestBody final CategoryDTO categoryDTO) {
		return categoriesService.create(categoryDTO);
	}

	@DeleteMapping("/{categoryId}")
	@Operation(summary = "Removes a Category by ID")
	public Mono<ResponseEntity<ErrorResponse>> deleteCategory(@PathVariable final long categoryId) {
		return categoriesService.delete(categoryId)
				.then(Mono.fromCallable(() -> new ResponseEntity<>(ErrorResponse.builder().message("Category successfully deleted").build(), HttpStatus.ACCEPTED)));
	}

	@PutMapping("/{categoryId}")
	@Operation(summary = "Updates a Category")
	public Mono<CategoryDTO> update(@PathVariable final long categoryId, @RequestBody final CategoryDTO categorysDTO) {
		return categoriesService.update(categoryId, categorysDTO);
	}

	@GetMapping("/name/{name}")
	@Operation(summary = "Find All Categories by name")
	public Flux<CategoryDTO> listByName(@PathVariable final String name) {
		return categoriesService.listByName(name)
					.sort(Comparator.comparing(CategoryDTO::name));
	}

}
