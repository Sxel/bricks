package com.example.bricks.controllers;



import com.example.bricks.model.Category;
import com.example.bricks.model.Product;
import com.example.bricks.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getProducts_ShouldReturnPageOfProducts() {
        Category category = new Category(1L, "Test Category");
        Product product = new Product(1L, "Test Product", 10.0, 5, category);
        Page<Product> productPage = new PageImpl<>(Arrays.asList(product));

        when(productService.getProducts(anyString(), anyDouble(), anyInt(), anyLong(), any(Pageable.class)))
                .thenReturn(productPage);

        ResponseEntity<Object> response = productController.getProducts("Test", 10.0, 5, 1L, Pageable.unpaged());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Page);
        Page<Product> resultPage = (Page<Product>) response.getBody();
        assertEquals(1, resultPage.getTotalElements());
        assertEquals("Test Product", resultPage.getContent().get(0).getName());
    }

    @Test
    void getProduct_ShouldReturnProduct() {
        Category category = new Category(1L, "Test Category");
        Product product = new Product(1L, "Test Product", 10.0, 5, category);

        when(productService.getProduct(1L)).thenReturn(product);

        ResponseEntity<Product> response = productController.getProduct(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test Product", response.getBody().getName());
    }

    @Test
    void createProduct_ShouldReturnCreatedProduct() {
        Category category = new Category(1L, "Test Category");
        Product product = new Product(null, "New Product", 15.0, 10, category);
        Product createdProduct = new Product(1L, "New Product", 15.0, 10, category);

        when(productService.createProduct(any(Product.class))).thenReturn(createdProduct);

        ResponseEntity<Product> response = productController.createProduct(product);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("New Product", response.getBody().getName());
    }
}