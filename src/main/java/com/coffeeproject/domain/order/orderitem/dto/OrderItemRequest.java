package com.coffeeproject.domain.order.orderitem.dto;

import jakarta.validation.constraints.Min;

public record OrderItemRequest(
        int productId,

        @Min(value = 1, message = "수량은 1 이상이어야 합니다.")
        int quantity
) {}