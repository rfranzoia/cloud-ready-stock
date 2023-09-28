package com.franzoia.stockservice.config;

import com.franzoia.common.dto.ProductDTO;
import com.franzoia.common.exception.EntityNotFoundException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(value = "PRODUCT-SERVICE", path = "/product-service/api/v1/products")
public interface ProductFeignClient {

    @GetMapping
    public List<ProductDTO> getAllProducts();

    @GetMapping("/{productId}")
    public ProductDTO getProductById(@PathVariable("productId") final Long productId) throws EntityNotFoundException;

}
