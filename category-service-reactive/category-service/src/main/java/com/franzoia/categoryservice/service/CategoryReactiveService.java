package com.franzoia.categoryservice.service;

import com.franzoia.categoryservice.common.reactive.DefaultReactiveService;
import com.franzoia.categoryservice.model.Category;
import com.franzoia.categoryservice.repository.CategoryRepository;
import com.franzoia.categoryservice.service.mapper.CategoryMapper;
import com.franzoia.common.dto.CategoryDTO;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CategoryReactiveService extends DefaultReactiveService<CategoryDTO, Category, Long, CategoryMapper> {

    public CategoryReactiveService(ReactiveCrudRepository<Category, Long> repository) {
        super(repository, new CategoryMapper());
    }

    public Mono<CategoryDTO> create(final CategoryDTO dto) {
        return save(mapper.convertDtoToEntity(dto))
                    .switchIfEmpty(constraintViolation)
                    .map(mapper::convertEntityToDTO);
    }

    public Mono<CategoryDTO> update(final Long categoryId, final CategoryDTO dto) {
        return repository.findById(categoryId)
                .switchIfEmpty(entityNotFound)
                .flatMap(category -> {
                    category.get
                    category.setName(dto.name());
                    return save(category);
                })
                .map(mapper::convertEntityToDTO);
    }

    public Flux<CategoryDTO> listByName(final String name) {
        return ((CategoryRepository) repository).findAllByNameLikeOrderByName(name)
                    .filter(category -> !category.getDeleted())
                    .map(mapper::convertEntityToDTO);
    }
}
