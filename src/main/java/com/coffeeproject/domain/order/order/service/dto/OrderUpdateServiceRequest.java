package com.coffeeproject.domain.order.order.service.dto;

import com.coffeeproject.domain.order.order.enums.OrderStatus;

import java.util.List;

public record OrderUpdateServiceRequest(
        String shippingAddress,
        String shippingZipCode,
        OrderStatus status,
        List<OrderUpdateItemServiceRequest> items
) {
    public record OrderUpdateItemServiceRequest(
            int productId,
            int quantity
    ) {
    }
}