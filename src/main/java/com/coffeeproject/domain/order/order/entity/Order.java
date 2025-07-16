package com.coffeeproject.domain.order.order.entity;

import com.coffeeproject.domain.order.order.enums.OrderStatus;
import com.coffeeproject.domain.order.orderitem.OrderItem;
import com.coffeeproject.global.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {
    @Column(length = 50, nullable = false)
    private String customerEmail;

    @Column(length = 100, nullable = false)
    private String shippingAddress;

    @Column(length = 20, nullable = false)
    private String shippingZipCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private int totalAmount;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    private Order(String customerEmail, String shippingAddress, String shippingZipCode) {
        this.customerEmail = customerEmail;
        this.shippingAddress = shippingAddress;
        this.shippingZipCode = shippingZipCode;
        this.status = OrderStatus.PAID;
    }

    public static Order createOrder(String customerEmail, String shippingAddress, String shippingZipCode) {
        return new Order(customerEmail, shippingAddress, shippingZipCode);
    }

    public void calculateTotalAmount() {
        this.totalAmount = orderItems.stream()
                .mapToInt(OrderItem::getSubtotalAmount)
                .sum();
    }


    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }
}
