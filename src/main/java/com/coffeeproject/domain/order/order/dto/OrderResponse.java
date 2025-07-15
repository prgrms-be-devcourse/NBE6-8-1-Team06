package com.coffeeproject.domain.order.order.dto;

import com.coffeeproject.domain.order.order.entity.Order;
import com.coffeeproject.domain.order.orderitem.dto.OrderItemResponse;

import java.util.List;

public record OrderResponse(
        int orderId,
        String customerEmail,
        String shippingAddress,
        String shippingZipCode,
        List<OrderItemResponse> items
) {
    public OrderResponse(Order order) {
        this(
                order.getId(),
                order.getCustomerEmail(),
                order.getShippingAddress(),
                order.getShippingZipCode(),
                order.getOrderItems()
                        .stream()
                        .map(OrderItemResponse::new)
                        .toList()
        );
    }
}
