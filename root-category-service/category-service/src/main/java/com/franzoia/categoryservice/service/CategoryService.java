package com.franzoia.categoryservice.service;

import com.franzoia.categoryservice.model.Category;
import com.franzoia.categoryservice.repository.CategoryRepository;
import com.franzoia.categoryservice.service.mapper.CategoryMapper;
import com.franzoia.common.dto.CategoryDTO;
import com.franzoia.common.exception.ConstraintsViolationException;
import com.franzoia.common.exception.EntityNotFoundException;
import com.franzoia.common.util.DefaultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.Predicate;


/**
 * Category Service class
 */
@Slf4j
@Service
public class CategoryService extends DefaultService<CategoryDTO, Category, Long, CategoryMapper> {

	public CategoryService(final CategoryRepository categoryRepository) {
		super(categoryRepository, new CategoryMapper());
	}

	@Transactional
	public CategoryDTO create(CategoryDTO dto) throws ConstraintsViolationException {
		log.info(String.format("create category %s", dto.toString()));
		if (hasDuplicateName(dto.name(), null)) {
			throw new ConstraintsViolationException("A category with the provided name already exists");
		}
		Category category = mapper.convertDtoToEntity(dto);
		return mapper.convertEntityToDTO(create(category));
	}

	/**
	 * Update a categorys information.
	 *
	 * @param categoryId
	 * @throws EntityNotFoundException
	 */
	@Transactional
	public CategoryDTO update(final Long categoryId, final CategoryDTO dto) throws EntityNotFoundException, ConstraintsViolationException {
		log.info(String.format("update category %s", dto.toString()));
		Category category = findByIdChecked(categoryId);
		if (hasDuplicateName(dto.name(), category)) {
			throw new ConstraintsViolationException("A category with the provided name already exists");
		}
		category.setName(dto.name() != null? dto.name(): category.getName());
		category.getAudit().setDateUpdated(ZonedDateTime.now());
		return mapper.convertEntityToDTO(repository.save(category));
	}

	private boolean hasDuplicateName(final String name, final Category category) {
		final Predicate<Category> notDeleted = c -> !c.getAudit().getDeleted();
		final Predicate<Category> equalName = c -> c.getName().equals(name);
		final Predicate<Category> sameCategory = c -> category == null || !c.getId().equals(category.getId());
		List<Category> list =
				findAll().stream()
					.filter(notDeleted
						.and(equalName)
						.and(sameCategory)).toList();
		return !list.isEmpty();
	}



	/**
	 * Locate all categorys by name or part of the name (min 2 characters)
	 *
	 * @param name the name of the category
	 * @return a list with all categories that match the name (starting with)
	 */
	public List<CategoryDTO> listByName(final String name) {
		log.info("list by category name");
		return mapper.convertEntityToDTO(((CategoryRepository) repository).findAllByNameLikeOrderByName(name));
	}

}
