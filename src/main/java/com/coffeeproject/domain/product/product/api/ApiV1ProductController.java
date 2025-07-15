package com.coffeeproject.domain.product.product.api;

import com.coffeeproject.domain.product.product.entity.Product;
import com.coffeeproject.domain.product.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ApiV1ProductController {
    private final ProductService productService;


    @PostMapping
    @Transactional
    public Product createProduct(Product product) {
        return productService.savegit(product);
    }
    @GetMapping
    @Transactional
    public List<Product> getAllProducts()
    {
        return productService.findAll();
    }

    @GetMapping({"/{id}"})
    @Transactional
    public Product getProductById(int id) {
        return productService.findById(id).orElse(null);
    }
}

