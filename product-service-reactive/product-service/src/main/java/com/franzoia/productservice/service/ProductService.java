package com.franzoia.productservice.service;

import com.franzoia.common.dto.CategoryDTO;
import com.franzoia.common.dto.ProductDTO;
import com.franzoia.common.exception.EntityNotFoundException;
import com.franzoia.common.util.reactive.DefaultReactiveService;
import com.franzoia.productservice.model.Product;
import com.franzoia.productservice.repository.ProductRepository;
import com.franzoia.productservice.service.mapper.ProductMapper;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;

/**
 * Services that handle Products (and their associated categories)
 * also, uses external category service
 */
@Slf4j
@Service
public class ProductService extends DefaultReactiveService<ProductDTO, Product, Long, ProductMapper> {

	final private CategoryService categoryService = new CategoryService();

	public ProductService(final ProductRepository productRepository) {
		super(productRepository, new ProductMapper());
	}

	final Map<Long, CategoryDTO> categoryMap = getCategoryMap();

	final Function<Long, CategoryDTO> cat = id -> {
		if (categoryMap == null || categoryMap.isEmpty() || !categoryMap.containsKey(id)) {
			return CategoryDTO.builder()
					.name("Unavailable Category Data")
					.build();
		} else {
			return categoryMap.get(id);
		}
	};

	public Mono<ProductDTO> get(final Long productId) {
		return repository.findById(productId)
					.switchIfEmpty(entityNotFound)
					.map(p -> ProductDTO.builder()
							.id(p.getId())
							.name(p.getName())
							.category(cat.apply(p.getCategoryId()))
							.price(p.getPrice())
							.unit(p.getUnit())
							.build());
	}

	final protected Mono<CategoryDTO> categoryNotFound = Mono.error(new EntityNotFoundException("Category not found"));

	/**
	 * Creates a product
	 */
	@Transactional
	public Mono<ProductDTO> create(ProductDTO dto) {
		return getCategoryDTO(dto.categoryId())
					.flatMap(c -> ((ProductRepository) repository).findByName(dto.name())
						.flatMap(p ->constraintViolation)
						.switchIfEmpty(repository.save(mapper.convertDtoToEntity(dto)))
						.map(product -> ProductDTO.builder()
							.id(product.getId())
							.name(product.getName())
							.category(c)
							.price(product.getPrice())
							.unit(product.getUnit())
							.build()));
	}

	private Mono<Product> findProductByName(final Long productId, final ProductDTO dto) {
		return ((ProductRepository) repository).findByName(dto.name())
				.filter(product -> productId != null && !product.getId().equals(productId))
				.flatMap(c -> constraintViolation);
	}

	/**
	 * Update a product information.
	 */
	@Transactional
	public Mono<ProductDTO> update(final Long productId, final ProductDTO dto) {
		return repository.findById(productId)
				.switchIfEmpty(entityNotFound)
				.then(findProductByName(productId, dto))
				.switchIfEmpty(repository.findById(productId)
					.flatMap(product -> {
						product.setName(dto.name() != null? dto.name(): product.getName());
						product.setCategoryId(dto.categoryId() != null? dto.categoryId(): product.getCategoryId());
						product.setUnit(dto.unit() != null? dto.unit(): product.getUnit());
						product.setPrice(dto.price() != null? dto.price(): product.getPrice());
						product.setDateUpdated(ZonedDateTime.now());
						return save(product);
					}))
				.map(product -> ProductDTO.builder()
						.id(product.getId())
						.name(product.getName())
						.category(cat.apply(product.getCategoryId()))
						.price(product.getPrice())
						.unit(product.getUnit())
						.build());
	}

	/**
	 * List all products
	 */
	public Flux<ProductDTO> listAll() {
		return repository.findAll()
				.map(p -> ProductDTO.builder()
							.id(p.getId())
							.name(p.getName())
							.category(cat.apply(p.getCategoryId()))
							.price(p.getPrice())
							.unit(p.getUnit())
							.build());
	}

	/**
	 * Locate all products by name or part of the name (min 2 characters)
	 */
	public Flux<ProductDTO> listByName(final String name) {
		return ((ProductRepository) repository).findAllByNameLikeOrderByName(name)
				.map(p -> ProductDTO.builder()
						.id(p.getId())
						.name(p.getName())
						.category(cat.apply(p.getCategoryId()))
						.price(p.getPrice())
						.unit(p.getUnit())
						.build());
	}

	/**
	 * Locate all products by category
	 */
	public Flux<ProductDTO> listByCategory(final Long categoryId) {
		return Flux.from(getCategoryDTO(categoryId))
				.flatMap(c -> ((ProductRepository) repository)
						.findAllByCategoryIdOrderByName(categoryId)
						.map(p -> ProductDTO.builder()
								.id(p.getId())
								.name(p.getName())
								.category(cat.apply(p.getCategoryId()))
								.price(p.getPrice())
								.unit(p.getUnit())
								.build()));
	}

	private Mono<CategoryDTO> getCategoryDTO(final Long categoryId) {
		return categoryService.getCategoryById(categoryId)
				.switchIfEmpty(categoryNotFound);
	}

	private Map<Long, CategoryDTO> getCategoryMap() {
		try {
			return categoryService.listCategories()
					.defaultIfEmpty(CategoryDTO.builder().build())
					.collectMap(CategoryDTO::id)
					.block();
		} catch (Throwable fe) {
			log.error("category-service status: {}", fe.getMessage());
			return new TreeMap<>();
		}

	}
}
