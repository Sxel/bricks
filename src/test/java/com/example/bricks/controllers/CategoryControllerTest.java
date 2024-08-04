package com.example.bricks.controllers;


import com.example.bricks.model.Category;
import com.example.bricks.services.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getCategories_ShouldReturnListOfCategories() {
        List<Category> categories = Arrays.asList(
                new Category(1L, "Category 1"),
                new Category(2L, "Category 2")
        );

        when(categoryService.getCategories()).thenReturn(categories);

        ResponseEntity<List<Category>> response = categoryController.getCategories();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("Category 1", response.getBody().get(0).getName());
        assertEquals("Category 2", response.getBody().get(1).getName());

        verify(categoryService, times(1)).getCategories();
    }
}