package com.franzoia.productservice.service;

import com.franzoia.common.dto.CategoryDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CategoryService {

    //@Autowired
    //private CategoryFeignClient categoryFeignClient;

    WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8083/category-service/api/v1/categories")
            .build();

    public Mono<CategoryDTO> getCategoryById(final Long categoryId) {
        return webClient.get().uri(String.format("/%d", categoryId))
                .retrieve().bodyToMono(CategoryDTO.class);
    }

    public Flux<CategoryDTO> listCategories() {
        return webClient.get().uri("")
                .retrieve().bodyToFlux(CategoryDTO.class);
    }
}
