package com.coffeeproject.domain.order.order.repository;

import com.coffeeproject.domain.order.order.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 임시 상품 레포지토리
 * 실제 상품 레포지토리 구현 시 삭제
 */
public interface ProductRepository extends JpaRepository<Product, Integer> {
}
