package com.coffeeproject.domain.order.order.service;

import com.coffeeproject.domain.order.order.dto.OrderRequest;
import com.coffeeproject.domain.order.order.entity.Order;
import com.coffeeproject.domain.order.order.repository.OrderRepository;
import com.coffeeproject.domain.order.orderitem.OrderItem;
import com.coffeeproject.domain.order.orderitem.dto.OrderItemRequest;
import com.coffeeproject.global.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orderRepository;

    public Order createOrder(OrderRequest request) {
        Order order = Order.createOrder(
                request.email(),
                request.address(),
                request.postalCode()
        );

        request.items().forEach(itemRequest -> {
            OrderItem orderItem = OrderItem.createOrderItem(order, itemRequest.quantity());
            order.addOrderItem(orderItem);
        });

        return orderRepository.save(order);
    }
}
