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

/**
 * 스케줄러를 통해 수행될 작업을 정의하는 클래스로 Runnable을 구현합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryBatchTask implements Runnable {
    private final DeliveryService deliveryService;
    private final OrderService orderService;

    @Override
    public void run() {
        log.info("스케줄링 작업 실행: {}", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        //1. 단 배송해야할 오더의 리스트를 반환한다.
        List<Order> list = orderService.getAllOrders().stream()
                .filter(order -> order.getStatus() == OrderStatus.PAID)
                .filter(order -> order.getCreateDate().isAfter(LocalDateTime.now().minusHours(24)))
                .toList();
        //2. 배송 엔티티로 만들어 저장한다.
        //FIXME : 이건 잘못된 사용법. 주문자와 주소가 동일한 Order끼리만 리스트로 만들어서 생성해야함
        Delivery delivery = deliveryService.createDelivery(list);
    }
}