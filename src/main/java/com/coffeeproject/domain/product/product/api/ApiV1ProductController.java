package com.coffeeproject.domain.product.product.api;

import com.coffeeproject.domain.product.product.dto.ProductDto;
import com.coffeeproject.domain.product.product.entity.Product;
import com.coffeeproject.domain.product.product.service.ProductService;
import com.coffeeproject.global.rsData.RsData;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ApiV1ProductController {
    private final ProductService productService;

    record ProductRequestBody(
            @NotBlank
            String name,
            @NotBlank
            String description,

            int price
    ) {}

    @PostMapping
    @Transactional
    public RsData<ProductDto> createProduct(@RequestBody ProductRequestBody reqBody) {
        Product product = productService.write(reqBody.name(), reqBody.description(), reqBody.price());
        return new RsData<>(
                "201",
                "상품이 등록되었습니다.",
                new ProductDto(product)
        );
    }
    @GetMapping
    @Transactional
    //@Optional (summary = "모든 상품 조회")
    public List<Product> getAllProducts()
    {
        return productService.findAll();
    }

    @GetMapping({"/{id}"})
    @Transactional
    public Product getProductById(@PathVariable Integer id) {
        return productService.findById(id).orElse(null);
    }}