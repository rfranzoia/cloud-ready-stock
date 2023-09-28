package com.franzoia.stockservice.service;

import com.franzoia.common.dto.CategoryDTO;
import com.franzoia.common.dto.ProductDTO;
import com.franzoia.common.exception.EntityNotFoundException;
import com.franzoia.common.exception.ServiceNotAvailableException;
import com.franzoia.stockservice.config.ProductFeignClient;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static java.util.stream.Collectors.groupingBy;

@Slf4j
@Component
public class ProductService {

    @Autowired
    private ProductFeignClient productFeignClient;

    public ProductDTO getProductById(final Long productId) throws EntityNotFoundException, ServiceNotAvailableException {
        try {
            return productFeignClient.getProductById(productId);
        } catch (FeignException fe) {
            log.error("product-service status: {}", fe.getMessage());
            throw fe;
        }
    }

    public Map<Long, List<ProductDTO>> getProductMap() {
        try {
            List<ProductDTO> products = productFeignClient.getAllProducts();
            return products.stream().collect(groupingBy(ProductDTO::id));
        } catch (Throwable fe) {
            log.error("product-service status: {}", fe.getMessage());
            return new TreeMap<>();
        }

    }

}
