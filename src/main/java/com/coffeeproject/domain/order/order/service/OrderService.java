package com.coffeeproject.domain.order.order.service;

import com.coffeeproject.domain.order.order.dto.OrderRequest;
import com.coffeeproject.domain.order.order.entity.Order;
import com.coffeeproject.domain.order.order.repository.OrderRepository;
import com.coffeeproject.domain.order.orderitem.OrderItem;
import com.coffeeproject.global.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orderRepository;

    @Transactional
    public Order createOrder(OrderRequest request) {
        Order order = Order.createOrder(
                request.customerEmail(),
                request.shippingAddress(),
                request.shippingZipCode()
        );

        request.items().forEach(itemRequest -> {
            OrderItem orderItem = OrderItem.createOrderItem(order, itemRequest.quantity());
            order.addOrderItem(orderItem);
        });

        order.calculateTotalAmount();

        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(int id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ServiceException("400", id + "번 주문이 존재하지 않습니다."));
    }
}
