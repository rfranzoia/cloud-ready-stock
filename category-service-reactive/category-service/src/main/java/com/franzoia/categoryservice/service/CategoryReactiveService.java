package com.franzoia.categoryservice.service;

import com.franzoia.categoryservice.model.Category;
import com.franzoia.categoryservice.repository.CategoryRepository;
import com.franzoia.categoryservice.service.mapper.CategoryMapper;
import com.franzoia.common.dto.CategoryDTO;
import com.franzoia.common.util.reactive.DefaultReactiveService;
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
        return ((CategoryRepository) repository).findByName(dto.name())
                .flatMap(c -> constraintViolation)
                .switchIfEmpty(save(mapper.convertDtoToEntity(dto)))
                .map(mapper::convertEntityToDTO);
    }

    public Mono<CategoryDTO> update(final Long categoryId, final CategoryDTO dto) {
        return repository.findById(categoryId)
                    .switchIfEmpty(entityNotFound)
                    .then(findCategoryByName(categoryId, dto))
                    .switchIfEmpty(repository.findById(categoryId)
                        .flatMap(category -> {
                            category.setName(dto.name());
                            return save(category);
                        }))
                    .map(mapper::convertEntityToDTO);

    }

    private Mono<Category> findCategoryByName(final Long categoryId, final CategoryDTO dto) {
        return ((CategoryRepository) repository).findByName(dto.name())
                .filter(category -> categoryId != null && !category.getId().equals(categoryId))
                .flatMap(c -> constraintViolation);
    }

    public Flux<CategoryDTO> listByName(final String name) {
        return ((CategoryRepository) repository)
                    .findAllByNameLikeOrderByName(name)
                        .map(mapper::convertEntityToDTO);
    }

}
