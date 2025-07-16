package com.coffeeproject.domain.product.product.dto;


import com.coffeeproject.domain.product.product.entity.Product;
import lombok.NonNull;

import java.time.LocalDateTime;

public record ProductDto(
        @NonNull int productId,
        @NonNull LocalDateTime created_at,
        @NonNull LocalDateTime updated_at,
        @NonNull String name,
        @NonNull String description,
        @NonNull int  price

){ public ProductDto(Product product)
    {
        this (
                product.getId(),
                product.getCreateDate(),
                product.getModifyDate(),
                product.getName(),
                product.getDescription(),
                product.getPrice()
        );
    }
}