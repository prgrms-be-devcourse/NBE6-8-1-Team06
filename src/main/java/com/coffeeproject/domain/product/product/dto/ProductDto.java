package com.coffeeproject.domain.product.product.dto;


import com.coffeeproject.domain.product.product.entity.Product;
import lombok.NonNull;

import java.time.LocalDateTime;

public record ProductDto(
        @NonNull String name,
        @NonNull String description,
        @NonNull int  price,
        @NonNull String imgUrl
){ public ProductDto(Product product)
    {
        this (
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getImgUrl()
        );
    }
}