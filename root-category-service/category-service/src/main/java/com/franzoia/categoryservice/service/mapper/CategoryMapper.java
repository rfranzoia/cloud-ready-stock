package com.franzoia.categoryservice.service.mapper;

import com.franzoia.categoryservice.model.Category;
import com.franzoia.common.dto.CategoryDTO;
import com.franzoia.common.util.AbstractMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CategoryMapper implements AbstractMapper<Category, CategoryDTO> {

    @Override
    public Category convertDtoToEntity(CategoryDTO dto) {
        return new Category(dto.id(), dto.name());
    }

    @Override
    public List<Category> convertDtoToEntity(List<CategoryDTO> dto) {
        return dto.stream().map(this::convertDtoToEntity).collect(Collectors.toList());
    }

    @Override
    public CategoryDTO convertEntityToDTO(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    @Override
    public List<CategoryDTO> convertEntityToDTO(List<Category> t) {
        return t.stream().map(this::convertEntityToDTO).collect(Collectors.toList());
    }
}
