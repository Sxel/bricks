package com.example.bricks.services;

import com.example.bricks.model.Category;
import com.example.bricks.repositories.CategoryRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;


import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CategoryRepository categoryRepository;


    private final String categoryApiUrl = "https://api.develop.bricks.com.ar/business/category";



    @PostConstruct
    public void syncCategories() {
        log.info("Iniciando sincronización de categorías");
        try {
            ResponseEntity<Category[]> response = restTemplate.getForEntity(categoryApiUrl, Category[].class);
            Category[] categories = response.getBody();
            if (categories != null) {
                categoryRepository.saveAll(Arrays.asList(categories));
                log.info("Sincronización de categorías completada. {} categorías actualizadas", categories.length);
            }
        } catch (Exception e) {
            log.error("Error al sincronizar categorías", e);
        }
    }

    @Cacheable(value = "categories")
    @CircuitBreaker(name = "categoryService", fallbackMethod = "getDefaultCategories")
    public List<Category> getCategories() {
        ResponseEntity<Category[]> response = restTemplate.getForEntity(categoryApiUrl, Category[].class);
        return Arrays.asList(response.getBody());
    }

    public List<Category> getDefaultCategories(Throwable t) {
        // Retorna una lista de categorías por defecto en caso de fallo
        return Arrays.asList(new Category(1L, "Default Category"));
    }

    public Category getCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
    }
}