package com.coffeeproject.domain.order.order.dto;

import com.coffeeproject.domain.order.order.entity.Order;
import com.coffeeproject.domain.order.orderitem.dto.OrderItemResponse;

import java.util.List;

public record OrderResponse(
        int orderId,
        String email,
        String address,
        String postalCode,
        List<OrderItemResponse> items
) {
    public OrderResponse(Order order) {
        this(
                order.getId(),
                order.getEmail(),
                order.getAddress(),
                order.getPostalCode(),
                order.getOrderItems()
                        .stream()
                        .map(OrderItemResponse::new)
                        .toList()
        );
    }
}
