package com.coffeeproject.domain.order.order.entity;

import com.coffeeproject.domain.order.order.enums.OrderStatus;
import com.coffeeproject.domain.order.orderitem.OrderItem;
import com.coffeeproject.global.exception.ServiceException;
import com.coffeeproject.global.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Builder
@AllArgsConstructor
@Table(name ="customer_order")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {
    @Column(length = 50, nullable = false)
    private String customerEmail;

    @Column(length = 100)
    private String shippingAddress;

    @Column(length = 20)
    private String shippingZipCode;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(nullable = false)
    private int totalPrice;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    private Order(String customerEmail, String shippingAddress, String shippingZipCode) {
        this.customerEmail = customerEmail;
        this.shippingAddress = shippingAddress;
        this.shippingZipCode = shippingZipCode;
        this.status = OrderStatus.PENDING;
    }

    public static Order createOrder(String customerEmail, String shippingAddress, String shippingZipCode) {
        return new Order(customerEmail, shippingAddress, shippingZipCode);
    }

    public void calculateTotalAmount() {
        this.totalPrice = orderItems.stream()
                .mapToInt(OrderItem::getSubtotalAmount)
                .sum();
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void updateShippingInfo(String shippingAddress, String shippingZipCode) {
        this.shippingAddress = shippingAddress;
        this.shippingZipCode = shippingZipCode;
    }

    public void updateStatus(OrderStatus status) {
        this.status = status;
    }

    public void paid() {
        if (this.status != OrderStatus.PENDING) {
            throw new ServiceException("400", "결제 대기 상태에서만 결제 완료 처리가 가능합니다. 현재 상태: " + status);
        }
        this.status = OrderStatus.PAID;
    }

    public void cancel() {
        this.status = OrderStatus.CANCELED;
    }
}
