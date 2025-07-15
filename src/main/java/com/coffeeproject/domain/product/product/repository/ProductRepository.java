package com.coffeeproject.domain.product.product.repository;

import com.coffeeproject.domain.product.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product,Integer> {

}
