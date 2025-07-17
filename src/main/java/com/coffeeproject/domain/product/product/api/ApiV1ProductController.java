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
    ) {
    }

    @PostMapping
    @Transactional
    public RsData<ProductDto> createProduct(@RequestBody ProductRequestBody reqBody) {

        Product product =productService.write(reqBody.name(), reqBody.description(), reqBody.price());
        return new RsData<>(
                "201",
                "상품이 등록되었습니다.",
                new ProductDto(product)
        );
    }

    @GetMapping
    @Transactional
    //@Optional (summary = "모든 상품 조회")
    public RsData<List<Product>> getAllProducts() {
        List<Product> products = productService.findAll();
        if (products.isEmpty()) {return   new RsData<>("404", "상품이 없습니다.", null);}
        else {
            return new RsData<>("200", "상품들을 찾았습니다.", products);
        }
    }

    @GetMapping({"/{id}"})
    @Transactional
    public RsData<Product> getProductById(@PathVariable(value = "id") int id) {
        Product product = productService.findById(id).orElse(null);
        if (product == null) {
            return new RsData<>("404", "상품을 찾을 수 없습니다.", null);
        }
        else {
            return new RsData<>("200", "상품을 찾았습니다.", product);

        }
    }

    @DeleteMapping({"/{id}"})
    @Transactional
    public RsData<ProductDto> deleteProductById(@PathVariable(value = "id") int id)
    {
        Product product = productService.findById(id).orElse(null);
        if (product == null) {return new RsData<>("404", "상품이 존재화지  않습니다.", null);}
        else {
            productService.deleteById(id);
            return new RsData<>("204", "상품이 삭제되었습니다.", null);
        }
    }

    @PutMapping("/{id}")
    @Transactional
    public RsData<ProductDto> updateProduct(
            @PathVariable(value = "id") int id,
            @RequestBody ProductRequestBody reqBody
    ) {
        Product product = productService.findById(id).orElse(null);
        if (product == null) {
            return new RsData<>("404", "상품을 찾을 수 없습니다.", null);
        }
        productService.modify(product, reqBody.name(), reqBody.description(), reqBody.price());
        return new RsData<>(
                "200",
                "상품이 수정되었습니다.",
                new ProductDto(product)
        );
    }

}