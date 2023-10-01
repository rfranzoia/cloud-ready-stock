package com.franzoia.productservice.controller;

import com.franzoia.common.dto.CategoryDTO;
import com.franzoia.common.dto.ProductDTO;
import com.franzoia.common.exception.ConstraintsViolationException;
import com.franzoia.common.exception.EntityNotFoundException;
import com.franzoia.common.exception.ServiceNotAvailableException;
import com.franzoia.common.util.ErrorResponse;
import com.franzoia.productservice.service.ProductService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * All operations with a product will be routed by this controller.
 * <p/>
 */
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

	private final ProductService productsService;

	@Autowired
	public ProductController(final ProductService productsService) {
		this.productsService = productsService;
	}

	@GetMapping
	public Flux<ProductDTO> listAll() {
		return productsService.listAll();
	}

	@GetMapping("/{productId}")
	public Mono<ProductDTO> getProduct(@PathVariable final long productId) {
		return productsService.get(productId);
	}

	@PostMapping
	public Mono<ProductDTO> createProduct(@RequestBody final ProductDTO productsDTO) {
		return productsService.create(productsDTO);
	}

	@DeleteMapping("/{productId}")
	public Mono<ResponseEntity<ErrorResponse>> deleteProduct(@PathVariable final long productId) {
		return productsService.delete(productId)
				.then(Mono.fromCallable(() -> new ResponseEntity<>(ErrorResponse.builder().message("Category successfully deleted").build(), HttpStatus.ACCEPTED)));
	}

	@PutMapping("/{productId}")
	public Mono<ProductDTO> update(@PathVariable final long productId, @RequestBody final ProductDTO productDTO) {
		return productsService.update(productId, productDTO);
	}

	@GetMapping("/name/{name}")
	public Flux<ProductDTO> listByName(@PathVariable final String name) {
		return productsService.listByName(name);
	}

	@GetMapping("/category/{categoryId}")
	public  Flux<ProductDTO> listByCategory(@PathVariable final Long categoryId) {
		return productsService.listByCategory(categoryId);
	}


}
