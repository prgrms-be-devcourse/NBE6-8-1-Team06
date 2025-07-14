package com.coffeeproject.domain.product.product.dto;

import com.domain.product.product.entity.Product;
import lombok.NonNull;

import java.time.LocalDateTime;

public record ProductDto(
        @NonNull Integer id,
        @NonNull LocalDateTime createDate,
        @NonNull LocalDateTime updateDate,
        @NonNull String name,
        @NonNull String description,
        @NonNull int  price,
        @NonNull String imageUrl
){ public ProductDto(Product product)
    {
        this (
                product.getId(),
                product.getCreateDate(),
                product.getModifyDate(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getImageUrl()
        );
    }
}