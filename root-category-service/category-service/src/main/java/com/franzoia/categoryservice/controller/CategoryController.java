package com.franzoia.categoryservice.controller;

import com.franzoia.categoryservice.service.CategoryService;
import com.franzoia.common.dto.CategoryDTO;
import com.franzoia.common.exception.ConstraintsViolationException;
import com.franzoia.common.exception.EntityNotFoundException;
import com.franzoia.common.exception.ServiceNotAvailableException;
import com.franzoia.common.util.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

/**
 * All operations with a category will be routed by this controller.
 * <p/>
 */
@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

	private final CategoryService categoriesService;

	@Autowired
	public CategoryController(final CategoryService categorysService) {
		this.categoriesService = categorysService;
	}

	@GetMapping
	public List<CategoryDTO> listAll() {
		return categoriesService.getMapper()
				.convertEntityToDTO(categoriesService.findAll()).stream()
					.sorted(Comparator.comparing(CategoryDTO::name)).toList();
	}

	@GetMapping("/{categoryId}")
	public CategoryDTO getCategory(@PathVariable final long categoryId) throws EntityNotFoundException {
		return categoriesService.getMapper().convertEntityToDTO(categoriesService.find(categoryId));
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CategoryDTO createCategory(@RequestBody final CategoryDTO categorysDTO) throws ConstraintsViolationException {
		return categoriesService.create(categorysDTO);
	}

	@DeleteMapping("/{categoryId}")
	public ResponseEntity<ErrorResponse> deleteCategory(@PathVariable final long categoryId) throws EntityNotFoundException, ServiceNotAvailableException {
		categoriesService.delete(categoryId);
		return new ResponseEntity<>(ErrorResponse.builder().message("Category successfully deleted").build(), HttpStatus.ACCEPTED);
	}

	@PutMapping("/{categoryId}")
	public CategoryDTO update(@PathVariable final long categoryId, @RequestBody final CategoryDTO categorysDTO)
			throws EntityNotFoundException, ConstraintsViolationException {
		return categoriesService.update(categoryId, categorysDTO);
	}

	@GetMapping("/name/{name}")
	public List<CategoryDTO> listByName(@PathVariable final String name) {
		return categoriesService.listByName(name);
	}

}
