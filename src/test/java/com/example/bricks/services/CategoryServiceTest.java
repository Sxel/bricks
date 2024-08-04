package com.example.bricks.services;



import com.example.bricks.component.CategoryMetrics;
import com.example.bricks.component.CategoryServiceLogger;
import com.example.bricks.model.Category;
import com.example.bricks.repositories.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.Supplier;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class CategoryServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMetrics categoryMetrics;

    @Mock
    private CategoryServiceLogger categoryServiceLogger;

    @Mock
    private CircuitBreakerFactory circuitBreakerFactory;

    @Mock
    private CircuitBreaker circuitBreaker;

    @InjectMocks
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(circuitBreakerFactory.create(anyString())).thenReturn(circuitBreaker);
        when(circuitBreaker.run(any(), any())).thenAnswer(invocation -> {
            return ((java.util.function.Supplier<?>) invocation.getArgument(0)).get();
        });
    }

    @Test
    void getCategories_ShouldReturnCategoriesFromAPI() {
        Category[] categories = {new Category(1L, "Category 1"), new Category(2L, "Category 2")};
        ResponseEntity<Category[]> responseEntity = ResponseEntity.ok(categories);

        when(restTemplate.getForEntity(anyString(), eq(Category[].class))).thenReturn(responseEntity);

        List<Category> result = categoryService.getCategories();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Category 1", result.get(0).getName());
        assertEquals("Category 2", result.get(1).getName());

        verify(categoryMetrics).incrementRequestCount();
        verify(categoryServiceLogger).logRequestSuccess(2);
        verify(categoryRepository).saveAll(Arrays.asList(categories));
    }


    @Test
    void getCategory_ShouldReturnCategoryWhenFound() {
        Category category = new Category(1L, "Test Category");
        when(categoryRepository.findById(1L)).thenReturn(java.util.Optional.of(category));

        Category result = categoryService.getCategory(1L);

        assertNotNull(result);
        assertEquals("Test Category", result.getName());
    }

    @Test
    void getCategory_ShouldThrowExceptionWhenNotFound() {
        when(categoryRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        assertThrows(RuntimeException.class, () -> categoryService.getCategory(1L));
    }
}