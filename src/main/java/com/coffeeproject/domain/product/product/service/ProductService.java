package com.coffeeproject.domain.product.product.service;


import com.coffeeproject.domain.product.product.entity.Product;
import com.coffeeproject.domain.product.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
public final ProductRepository productRepository;

    public Product write(String name,String description,int price) {
        Product product = new Product(name, description, price);
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


}
