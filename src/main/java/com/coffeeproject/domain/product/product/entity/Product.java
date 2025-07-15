package com.coffeeproject.domain.product.product.entity;

import com.coffeeproject.global.jpa.entity.BaseEntity;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Product extends BaseEntity {

    private String author;
    private String name;
    private String description;
    private int price;
}

