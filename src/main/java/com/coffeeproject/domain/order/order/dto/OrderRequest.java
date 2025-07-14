package com.coffeeproject.domain.order.order.dto;

import com.coffeeproject.domain.order.orderitem.dto.OrderItemRequest;

import java.util.List;

public record OrderRequest(
        String email,
        String address,
        String postalCode,
        List<OrderItemRequest> items
) {}
