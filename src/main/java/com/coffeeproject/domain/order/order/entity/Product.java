package com.coffeeproject.domain.order.order.entity;

import com.coffeeproject.global.jpa.entity.BaseEntity;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 임시로 연관관계 설정을 위한 상품 엔티티
 * 실제 상품 엔티티 구현 시 해당 엔티티 삭제
 */

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Product extends BaseEntity {

    private String name;
    private String description;
    private int price;
}