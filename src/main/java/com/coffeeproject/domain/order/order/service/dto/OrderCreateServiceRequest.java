package com.coffeeproject.domain.order.order.service.dto;


import java.util.List;

public record OrderCreateServiceRequest(
        String customerEmail,
        String shippingAddress,
        String shippingZipCode,
        List<OrderItemServiceRequest> items
) {
    public record OrderItemServiceRequest(int productId, int quantity) {}
}
