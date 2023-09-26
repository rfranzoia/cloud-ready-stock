package com.franzoia.stockservice.service;

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
        } catch (Throwable throwable) {
            log.error("Product not found, error {}", throwable.getMessage());
            if (throwable instanceof FeignException) {
                throw new ServiceNotAvailableException(String.format("Couldn't validate Product, is service down (?): %s", throwable.getMessage()));
            }
            throw new EntityNotFoundException("Product not found");
        }
    }

    public Map<Long, List<ProductDTO>> getProductMap() {
        try {
            List<ProductDTO> products = productFeignClient.getAllProducts();
            return products.stream().collect(groupingBy(ProductDTO::id));
        } catch (Throwable throwable) {
            log.error("Couldn't retrieve Product list, is product-service down (?) {}", throwable.getMessage());
            return new TreeMap<>();
        }
    }

}
