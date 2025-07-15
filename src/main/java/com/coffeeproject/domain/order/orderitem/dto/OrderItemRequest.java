package com.coffeeproject.domain.order.orderitem.dto;

public record OrderItemRequest(
        int productId,
        int quantity
) {}