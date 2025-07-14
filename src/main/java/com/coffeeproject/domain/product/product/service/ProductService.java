package com.coffeeproject.domain.product.product.service;


import com.domain.product.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {
public final ProductRepository productRepository;

}
