package com.example.bricks.services;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.named.NamedContextFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.Optional;

import com.example.bricks.model.Product;
import com.example.bricks.model.Category;
import com.example.bricks.repositories.ProductRepository;


@SpringBootTest
public class ProductServiceTest {
    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;



    @Test
    void createProductTest() {
        Product product = new Product();
        product.setName("New Product");
        product.setCategory(new Category(1L, "Test Category"));

        when(categoryService.getCategory(1L)).thenReturn(new Category(1L, "Test Category"));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product result = productService.createProduct(product);

        assertEquals("New Product", result.getName());
    }

    @Test
    void getProductTest() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Product result = productService.getProduct(1L);

        assertEquals("Test Product", result.getName());
    }

    @Test
    void deleteProductTest() {
        Long productId = 1L;

        when(productRepository.existsById(productId)).thenReturn(true);

        assertDoesNotThrow(() -> productService.deleteProduct(productId));

        verify(productRepository, times(1)).deleteById(productId);
    }
}