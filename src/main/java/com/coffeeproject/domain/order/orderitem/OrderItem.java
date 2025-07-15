package com.coffeeproject.domain.order.orderitem;

import com.coffeeproject.domain.order.order.entity.Order;
import com.coffeeproject.domain.order.order.entity.Product;
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

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private int pricePerItem;

    @Column(nullable = false)
    private int subtotalAmount;

    public void setOrder(Order order) {
        this.order = order;
    }

    private OrderItem(Product product, Order order, Integer quantity) {
        this.product = product;
        this.order = order;
        this.quantity = quantity;
        this.pricePerItem = product.getPrice();
        this.subtotalAmount = this.pricePerItem * quantity;
    }

    public static OrderItem createOrderItem(Product product, Order order, Integer quantity) {
        return new OrderItem(product, order, quantity);
    }
}
