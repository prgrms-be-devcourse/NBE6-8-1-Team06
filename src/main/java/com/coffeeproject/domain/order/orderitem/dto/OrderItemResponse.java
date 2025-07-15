package com.coffeeproject.domain.order.orderitem.dto;

import com.coffeeproject.domain.order.orderitem.OrderItem;

public record OrderItemResponse(
        int productId,
        String productName,
        int quantity
) {
    // TODO : 연관관계 설정 후 productId, productName 추가
    public OrderItemResponse(OrderItem orderItem) {
        this(
                1,
                "Sample Product",
                orderItem.getQuantity()
        );
    }
}
