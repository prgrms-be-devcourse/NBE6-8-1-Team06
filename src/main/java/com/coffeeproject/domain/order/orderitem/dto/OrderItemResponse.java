package com.coffeeproject.domain.order.orderitem.dto;

import com.coffeeproject.domain.order.orderitem.OrderItem;

public record OrderItemResponse(
        int productId,
        String productName,
        int quantity
) {
    public OrderItemResponse(OrderItem orderItem) {
        this(
                orderItem.getProduct().getId(),
                orderItem.getProduct().getName(),
                orderItem.getQuantity()
        );
    }
}
