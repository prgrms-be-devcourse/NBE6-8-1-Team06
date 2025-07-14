package com.coffeeproject.domain.order.orderitem.dto;

public record OrderItemRequest(
        Integer productId,
        Integer quantity
) {}