package com.coffeeproject.domain.product.product.entity;

import com.coffeeproject.global.jpa.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Product extends BaseEntity {

    private String name;
    private String description;
    private int price;
    public Product() {
    }
    public Product(@NotBlank String name, @NotBlank String description, @NotBlank int price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

}

