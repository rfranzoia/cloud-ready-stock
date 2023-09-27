package com.franzoia.productservice.controller;

import com.franzoia.common.dto.CategoryDTO;
import com.franzoia.common.dto.ProductDTO;
import com.franzoia.common.exception.ConstraintsViolationException;
import com.franzoia.common.exception.EntityNotFoundException;
import com.franzoia.common.exception.ServiceNotAvailableException;
import com.franzoia.common.util.ErrorResponse;
import com.franzoia.productservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
	public List<ProductDTO> listAll() {
		return productsService.listAll();
	}

	@GetMapping("/{productId}")
	public ProductDTO getProduct(@PathVariable final long productId) throws EntityNotFoundException, ServiceNotAvailableException {
		return productsService.get(productId);
	}

	@PostMapping
	public ProductDTO createProduct(@RequestBody final ProductDTO productsDTO) throws EntityNotFoundException, ConstraintsViolationException, ServiceNotAvailableException {
		return productsService.create(productsDTO);
	}

	@DeleteMapping("/{productId}")
	public ResponseEntity<ErrorResponse> deleteProduct(@PathVariable final long productId) throws EntityNotFoundException {
		productsService.delete(productId);
		return new ResponseEntity<>(ErrorResponse.builder()
				.message("Product Successfully deleted")
				.build(), HttpStatus.ACCEPTED);
	}

	@PutMapping("/{productId}")
	public ProductDTO update(@PathVariable final long productId, @RequestBody final ProductDTO productDTO)
			throws EntityNotFoundException, ConstraintsViolationException, ServiceNotAvailableException {
		return productsService.update(productId, productDTO);
	}

	@GetMapping("/name/{name}")
	public List<ProductDTO> listByName(@PathVariable final String name) {
		return productsService.listByName(name);
	}

	@GetMapping("/category/{categoryId}")
	public Map<CategoryDTO, List<ProductDTO>> listByCategory(@PathVariable final Long categoryId) throws EntityNotFoundException, ServiceNotAvailableException {
		return productsService.listByCategory(categoryId);
	}


}
