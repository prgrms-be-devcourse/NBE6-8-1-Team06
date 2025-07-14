package com.coffeeproject.domain.order.orderitem;

import com.coffeeproject.domain.order.order.entity.Order;
import com.coffeeproject.global.jpa.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

    // TODO : 상품 엔티티 @ManyToOne 연관관계 설정 필요

    @Column(nullable = false)
    private Integer quantity;

    public void setOrder(Order order) {
        this.order = order;
    }

    private OrderItem(Order order, Integer quantity) {
        this.order = order;
        this.quantity = quantity;
    }

    public static OrderItem createOrderItem(Order order, Integer quantity) {
        return new OrderItem(order, quantity);
    }
}
