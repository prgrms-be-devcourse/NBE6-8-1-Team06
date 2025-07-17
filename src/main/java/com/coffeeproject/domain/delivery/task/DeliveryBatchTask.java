package com.coffeeproject.domain.delivery.task;

import com.coffeeproject.domain.delivery.entity.Delivery;
import com.coffeeproject.domain.delivery.service.DeliveryService;
import com.coffeeproject.domain.order.order.entity.Order;
import com.coffeeproject.domain.order.order.enums.OrderStatus;
import com.coffeeproject.domain.order.order.service.OrderService;
import com.coffeeproject.domain.order.orderitem.OrderItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 스케줄러를 통해 수행될 작업을 정의하는 클래스로 Runnable을 구현합니다.
 */
@Component
@RequiredArgsConstructor
public class DeliveryBatchTask implements Runnable {
    private final DeliveryService deliveryService;
    private final OrderService orderService;

    record key(String eMail, String address) {}

    @Override
    public void run() {
        Map<key, List<Order>> orders = orderService.getAllOrders().stream()
                .filter(order -> order.getStatus() == OrderStatus.PAID)
                .filter(order -> order.getCreateDate().isAfter(LocalDateTime.now().minusHours(24)))
                .collect(Collectors.groupingBy(
                        order -> new key(order.getCustomerEmail(), order.getShippingAddress())
                ));

        for (List<Order> order : orders.values()) {
            deliveryService.createDelivery(order);
        }
    }
}