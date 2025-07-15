package com.coffeeproject.domain.order.order.service;

import com.coffeeproject.domain.order.order.dto.OrderRequest;
import com.coffeeproject.domain.order.order.entity.Order;
import com.coffeeproject.domain.order.order.repository.OrderRepository;
import com.coffeeproject.domain.order.orderitem.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orderRepository;

    public Order createOrder(OrderRequest request) {
        Order order = Order.createOrder(
                request.customerEmail(),
                request.shippingAddress(),
                request.shippingZipCode(),
                request.totalAmount()
        );

        request.items().forEach(itemRequest -> {
            OrderItem orderItem = OrderItem.createOrderItem(order, itemRequest.quantity());
            order.addOrderItem(orderItem);
        });

        return orderRepository.save(order);
    }
}
