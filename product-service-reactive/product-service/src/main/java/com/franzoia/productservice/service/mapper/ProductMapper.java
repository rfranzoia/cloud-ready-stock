package com.franzoia.productservice.service.mapper;

import com.franzoia.common.dto.ProductDTO;
import com.franzoia.common.util.AbstractMapper;
import com.franzoia.productservice.model.Product;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper implements AbstractMapper<Product, ProductDTO> {

    @Override
    public Product convertDtoToEntity(ProductDTO dto) {
        return new Product(dto.id(), dto.name(), dto.categoryId(), dto.unit(), dto.price());
    }

    @Override
    public List<Product> convertDtoToEntity(List<ProductDTO> dto) {
        return dto.stream().map(this::convertDtoToEntity).collect(Collectors.toList());
    }

    @Override
    public ProductDTO convertEntityToDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .categoryId(product.getCategoryId())
                .unit(product.getUnit())
                .price(product.getPrice())
                .build();
    }

    @Override
    public List<ProductDTO> convertEntityToDTO(List<Product> t) {
        return t.stream().map(this::convertEntityToDTO).collect(Collectors.toList());
    }
}
