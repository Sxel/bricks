package com.example.bricks.services;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;

import com.example.bricks.model.Category;

@SpringBootTest
public class CategoryServiceTest {
    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private CategoryService categoryService;

    @Test
    void getCategoriesTest() {
        Category[] categories = {new Category(1L, "Category 1"), new Category(2L, "Category 2")};
        ResponseEntity<Category[]> responseEntity = new ResponseEntity<>(categories, HttpStatus.OK);

        when(restTemplate.getForEntity(anyString(), eq(Category[].class))).thenReturn(responseEntity);

        List<Category> result = categoryService.getCategories();

        assertEquals(2, result.size());
        assertEquals("Category 1", result.get(0).getName());
    }

    @Test
    void getDefaultCategoriesTest() {
        when(restTemplate.getForEntity(anyString(), eq(Category[].class))).thenThrow(new RestClientException("API Error"));

        List<Category> result = categoryService.getDefaultCategories(new RestClientException("API Error"));

        assertEquals(1, result.size());
        assertEquals("Default Category", result.get(0).getName());
    }
}