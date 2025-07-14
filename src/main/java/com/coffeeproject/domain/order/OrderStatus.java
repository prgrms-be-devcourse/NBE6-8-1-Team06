package com.coffeeproject.domain.order;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PENDING("주문 접수 대기"),
    CONFIRMED("주문 확인 완료"),
    CANCELLED("주문 취소");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }
}
