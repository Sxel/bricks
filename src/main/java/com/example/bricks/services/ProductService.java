package com.example.bricks.services;

import com.example.bricks.model.Category;
import com.example.bricks.model.Product;
import com.example.bricks.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    @Autowired
    private final ProductRepository productRepository;
    @Autowired
    private final CategoryService categoryService;


    public Page<Product> getProducts(String name, Double price, Integer stock, Long categoryId, Pageable pageable) {
        Specification<Product> spec = Specification.where(null);

        if (name != null && !name.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        }
        if (price != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("price"), price));
        }
        if (stock != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("stock"), stock));
        }
        if (categoryId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("category").get("id"), categoryId));
        }

        return productRepository.findAll(spec, pageable);
    }

    public Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    }

    public Product createProduct(Product product) {
        log.info("Creando nuevo producto: {}", product);
        Category category = categoryService.getCategory(product.getCategory().getId());
        product.setCategory(category);
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
        productRepository.deleteById(id);
    }

    public Product updateProduct(Long id, Product productDetails) {
        Product product = getProduct(id);
        product.setName(productDetails.getName());
        product.setPrice(productDetails.getPrice());
        product.setStock(productDetails.getStock());
        if (!product.getCategory().getId().equals(productDetails.getCategory().getId())) {
            categoryService.getCategory(productDetails.getCategory().getId()); // Verifica que la nueva categoría existe
            product.setCategory(productDetails.getCategory());
        }
        return productRepository.save(product);
    }
}