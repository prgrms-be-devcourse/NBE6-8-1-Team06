package com.coffeeproject.domain.order.order.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PENDING("결제 전"),
    PAID("결제 완료"),
    CANCELED("결제 취소");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }
}
