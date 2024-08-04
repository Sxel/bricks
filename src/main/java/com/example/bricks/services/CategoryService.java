package com.example.bricks.services;

import com.example.bricks.component.CategoryMetrics;
import com.example.bricks.component.CategoryServiceLogger;
import com.example.bricks.model.Category;
import com.example.bricks.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.web.client.RestTemplate;
import java.util.Arrays;
import java.util.List;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {
    private final RestTemplate restTemplate;
    private final CategoryRepository categoryRepository;
    private final CategoryMetrics categoryMetrics;
    private final CategoryServiceLogger categoryServiceLogger;
    private final CircuitBreakerFactory circuitBreakerFactory;

    private final String categoryApiUrl = "https://api.develop.bricks.com.ar/business/category";

    private AtomicInteger dailyRequestCount = new AtomicInteger(0);
    private LocalDate lastRequestDate = LocalDate.now();

    @Cacheable(value = "categories", key = "'allCategories'")
    public List<Category> getCategories() {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("categoryService");

        return circuitBreaker.run(() -> {
            if (canMakeRequest()) {
                categoryServiceLogger.logRequestAttempt();
                categoryMetrics.incrementRequestCount();
                ResponseEntity<Category[]> response = restTemplate.getForEntity(categoryApiUrl, Category[].class);
                List<Category> categories = Arrays.asList(response.getBody());
                categoryRepository.saveAll(categories);
                categoryServiceLogger.logRequestSuccess(categories.size());
                return categories;
            } else {
                categoryServiceLogger.logRequestLimited();
                categoryMetrics.incrementLimitedRequestCount();
                return getDefaultCategories();
            }
        }, throwable -> {
            categoryServiceLogger.logRequestFailure(throwable);
            return getDefaultCategories();
        });
    }

    private boolean canMakeRequest() {
        LocalDate currentDate = LocalDate.now();
        if (!currentDate.equals(lastRequestDate)) {
            dailyRequestCount.set(0);
            lastRequestDate = currentDate;
        }
        return dailyRequestCount.incrementAndGet() <= 10;
    }

    private List<Category> getDefaultCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }
}