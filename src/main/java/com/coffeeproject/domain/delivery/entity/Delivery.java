package com.coffeeproject.domain.delivery.entity;

import com.coffeeproject.domain.order.order.entity.Order;
import com.coffeeproject.global.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Delivery extends BaseEntity {
    private String customerEmail;
    private String shippingAddress;
    /**
     * 오더와 단방향으로 연관관계를 구현하기 위해 @JoinTable을 사용했습니다.
     * 이렇게 생성된 테이블은 두 엔티티의 ID를 외래키로 가집니다.
     * Delevery 엔티티에서 Order 엔티티로의 참조는 가능하지만 반대는 불가능합니다.
     */
    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "delivery_orders",
            joinColumns = @JoinColumn(name = "delivery_id"),
            inverseJoinColumns = @JoinColumn(name = "order_id")
    )
    private List<Order> orders;
    private int totalPrice;
    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;

    public enum DeliveryStatus {
        PENDING,
        SHIPPED,
        DELIVERED,
        CANCELLED
    }

    public void updateShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public void updateStatus(DeliveryStatus status) {
        this.status = status;
    }

    public void updateTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }
}
