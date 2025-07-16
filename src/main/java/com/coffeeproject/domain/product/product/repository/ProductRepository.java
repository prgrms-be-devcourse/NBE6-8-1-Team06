package com.coffeeproject.domain.product.product.repository;

import com.coffeeproject.domain.product.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product,Integer> {
    Optional<Product> findFirstByOrderByIdDesc();
}
