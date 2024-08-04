package com.example.bricks.services;

import com.example.bricks.model.Category;
import com.example.bricks.repositories.CategoryRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CategoryRepository categoryRepository;

    private final String categoryApiUrl = "https://api.develop.bricks.com.ar/business/category";

    private AtomicInteger dailyRequestCount = new AtomicInteger(0);
    private LocalDate lastRequestDate = LocalDate.now();

    @Cacheable(value = "categories", key = "'allCategories'")
    @CircuitBreaker(name = "categoryService", fallbackMethod = "getDefaultCategories")
    public List<Category> getCategories() {
        if (canMakeRequest()) {
            ResponseEntity<Category[]> response = restTemplate.getForEntity(categoryApiUrl, Category[].class);
            List<Category> categories = Arrays.asList(response.getBody());
            categoryRepository.saveAll(categories);
            return categories;
        } else {
            log.warn("Daily request limit reached. Returning categories from local database.");
            return categoryRepository.findAll();
        }
    }

    private boolean canMakeRequest() {
        LocalDate currentDate = LocalDate.now();
        if (!currentDate.equals(lastRequestDate)) {
            dailyRequestCount.set(0);
            lastRequestDate = currentDate;
        }
        return dailyRequestCount.incrementAndGet() <= 10;
    }

    public List<Category> getDefaultCategories(Throwable t) {
        log.error("Error fetching categories from external API", t);
        return categoryRepository.findAll();
    }

    public Category getCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }
}