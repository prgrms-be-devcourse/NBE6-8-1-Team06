package com.coffeeproject.domain.product.product.service;


import com.coffeeproject.domain.product.product.entity.Product;
import com.coffeeproject.domain.product.product.repository.ProductRepository;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
public final ProductRepository productRepository;

    public Product write(String name,String description,String imgUrl ,int price) {
        Product product = new Product(name, description,imgUrl, price);
        return productRepository.save(product);
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Optional<Product> findById(int id) {
        return productRepository.findById(id);
    }
    public int count() {
        return (int) productRepository.count();
    }


    public Optional<Product> findLatest() {

        return productRepository.findFirstByOrderByIdDesc();
    }


    public void modifyById(int id ,Product product, @NotBlank String name, @NotBlank String description,@NotBlank String imgUrl, int price) {
        Product  tProduct = new Product(name, description,imgUrl, price);
        productRepository.findById(id).ifPresent(existingProduct -> {
            existingProduct.setName(tProduct.getName());
            existingProduct.setDescription(tProduct.getDescription());
            existingProduct.setPrice(tProduct.getPrice());
            existingProduct.setImgUrl(tProduct.getImgUrl());
            productRepository.save(existingProduct);
        });

    }

    public void deleteById(int id) {
        productRepository.deleteById(id);

    }

}
