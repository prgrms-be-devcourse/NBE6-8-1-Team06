package com.coffeeproject.domain.order.order.entity;

import com.coffeeproject.domain.order.order.enums.OrderStatus;
import com.coffeeproject.global.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {
    @Column(length = 50, nullable = false)
    private String email;

    @Column(length = 50, nullable = false)
    private String address;

    @Column(length = 50, nullable = false)
    private String postalCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

}
