package com.example.bricks.services;


import com.example.bricks.model.Category;
import com.example.bricks.model.Product;
import com.example.bricks.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getProducts_ShouldReturnPageOfProducts() {
        Category category = new Category(1L, "Test Category");
        Product product = new Product(1L, "Test Product", 10.0, 5, category);
        Page<Product> productPage = new PageImpl<>(Arrays.asList(product));

        when(productRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(productPage);

        Page<Product> result = productService.getProducts("Test", 10.0, 5, 1L, Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Product", result.getContent().get(0).getName());
    }

    @Test
    void getProduct_ShouldReturnProductWhenFound() {
        Category category = new Category(1L, "Test Category");
        Product product = new Product(1L, "Test Product", 10.0, 5, category);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Product result = productService.getProduct(1L);

        assertNotNull(result);
        assertEquals("Test Product", result.getName());
    }

    @Test
    void getProduct_ShouldThrowExceptionWhenNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> productService.getProduct(1L));
    }

    @Test
    void createProduct_ShouldReturnCreatedProduct() {
        Category category = new Category(1L, "Test Category");
        Product product = new Product(null, "New Product", 15.0, 10, category);
        Product savedProduct = new Product(1L, "New Product", 15.0, 10, category);

        when(categoryService.getCategory(1L)).thenReturn(category);
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        Product result = productService.createProduct(product);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("New Product", result.getName());
    }
}