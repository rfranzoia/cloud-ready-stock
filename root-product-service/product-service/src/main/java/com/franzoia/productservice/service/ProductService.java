package com.franzoia.productservice.service;

import com.franzoia.common.dto.CategoryDTO;
import com.franzoia.common.dto.ProductDTO;
import com.franzoia.common.exception.ConstraintsViolationException;
import com.franzoia.common.exception.EntityNotFoundException;
import com.franzoia.common.exception.ServiceNotAvailableException;
import com.franzoia.common.util.DefaultService;
import com.franzoia.productservice.config.CategoryFeignClient;
import com.franzoia.productservice.model.Product;
import com.franzoia.productservice.repository.ProductRepository;
import com.franzoia.productservice.service.mapper.ProductMapper;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.groupingBy;


/**
 * Services that handle Products (and their associated categories)
 * also, uses external category service
 */
@Slf4j
@Service
public class ProductService extends DefaultService<ProductDTO, Product, Long, ProductMapper> {

	@Autowired
	private CategoryFeignClient categoryFeignClient;

	public ProductService(final ProductRepository productRepository) {
		super(productRepository, new ProductMapper());
	}

	public ProductDTO get(final Long productId) throws EntityNotFoundException, ServiceNotAvailableException {
		Product product = findByIdChecked(productId);
		CategoryDTO category = getCategoryDTO(product.getCategoryId());
		return ProductDTO.builder()
				.id(product.getId())
				.name(product.getName())
				.category(category)
				.unit(product.getUnit())
				.price(product.getPrice())
				.build();
	}

	private CategoryDTO getCategoryDTO(final Long categoryId) throws EntityNotFoundException, FeignException, ServiceNotAvailableException {
		try {
			return categoryFeignClient.getByCategoryId(categoryId);
		} catch (ServiceNotAvailableException | FeignException fe) {
			log.error("Category not available", fe);
			throw fe;
        }
    }

	/**
	 * Creates a product
	 */
	@Transactional
	public ProductDTO create(ProductDTO dto) throws EntityNotFoundException, ConstraintsViolationException, ServiceNotAvailableException {
		CategoryDTO category = getCategoryDTO(dto.categoryId());

		if (hasDuplicateName(dto.name(), null)) {
			throw new ConstraintsViolationException("Product name already exists in the database");
		}

		Product product = create(mapper.convertDtoToEntity(dto));
		return ProductDTO.builder()
				.id(product.getId())
				.name(product.getName())
				.category(category)
				.price(product.getPrice())
				.unit(product.getUnit())
				.build();
	}

	public boolean hasDuplicateName(final String name, final Product product) {
		List<ProductDTO> products = listByName(name).stream()
				.filter(p -> product == null || !p.id().equals(product.getId()))
				.toList();
        return !products.isEmpty();
    }

	/**
	 * Update a product information.
	 */
	@Transactional
	public ProductDTO update(final Long productId, final ProductDTO dto) throws EntityNotFoundException, ConstraintsViolationException, ServiceNotAvailableException {
		Product product = findByIdChecked(productId);

		if (hasDuplicateName(dto.name(), product)) {
			throw new ConstraintsViolationException("Product name already exists in the database");
		}

		product.setName(dto.name() != null? dto.name(): product.getName());
		product.setCategoryId(dto.categoryId() != null? dto.categoryId(): product.getCategoryId());
		product.setUnit(dto.unit() != null? dto.unit(): product.getUnit());
		product.setPrice(dto.price() != null? dto.price(): product.getPrice());
		product.getAudit().setDateUpdated(ZonedDateTime.now());

		repository.save(product);

		return get(productId);
	}

	/**
	 * List all products
	 */
	public List<ProductDTO> listAll() {
		return createProductList(findAll(), null);
	}

	/**
	 * Locate all products by name or part of the name (min 2 characters)
	 */
	public List<ProductDTO> listByName(final String name) {
		return createProductList(((ProductRepository) repository).findAllByNameLikeOrderByName(name), null);
	}

	/**
	 * Locate all products by category
	 */
	public Map<CategoryDTO, List<ProductDTO>> listByCategory(final Long categoryId) throws EntityNotFoundException, ServiceNotAvailableException {
		// implicit category check
		CategoryDTO category = getCategoryDTO(categoryId);

		// find all products by category and return them grouping by Category
		return createProductList(((ProductRepository) repository).findAllByCategoryIdOrderByName(categoryId), category)
				.stream()
				.collect(groupingBy(ProductDTO::category));
	}

	private List<ProductDTO> createProductList(final List<Product> products, final CategoryDTO category) {
		Map<Long, List<CategoryDTO>> categoryMap = category == null? getCategoryMap(): null;

		// fallback for category-service
		final Function<Long, CategoryDTO> cat = id -> {
			if (categoryMap == null || categoryMap.isEmpty() || !categoryMap.containsKey(id)) {
				return CategoryDTO.builder()
						.name("Unavailable Category Data")
						.build();
			} else {
				return categoryMap.get(id).get(0);
			}
		};

		List<ProductDTO> list = new ArrayList<>();
		products.stream()
			.filter(p -> !p.getAudit().getDeleted())
			.forEach(p -> {
				ProductDTO dto = ProductDTO.builder()
						.id(p.getId())
						.name(p.getName())
						.category(category != null? category: cat.apply(p.getCategoryId()))
						.price(p.getPrice())
						.unit(p.getUnit())
						.build();
				list.add(dto);
			});
		return list.stream().sorted(Comparator.comparing(ProductDTO::name)).toList();
	}

	private Map<Long, List<CategoryDTO>> getCategoryMap() {
		try {
			List<CategoryDTO> categories = categoryFeignClient.listCategories();
			return categories.stream().collect(groupingBy(CategoryDTO::id));
		} catch (Throwable fe) {
			log.error("category-service status: {}", fe.getMessage());
			return new TreeMap<>();
		}

	}
}
