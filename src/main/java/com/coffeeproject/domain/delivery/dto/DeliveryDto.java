package com.coffeeproject.domain.delivery.dto;

import com.coffeeproject.domain.delivery.entity.Delivery;
import com.coffeeproject.domain.delivery.entity.Order;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record DeliveryDto(
        @NotBlank int id,
        @NotBlank String customerEmail,
        @NotBlank String shippingAddress,
        @NotBlank String status,
        @NotBlank List<Order> orderItems
) {
    public DeliveryDto(Delivery delivery) {
        this(
                delivery.getId(),
                delivery.getCustomerEmail(),
                delivery.getShippingAddress(),
                delivery.getStatus().name(),
                delivery.getOrders()
        );
    }
}
