package com.coffeeproject.domain.delivery.task;

import com.coffeeproject.domain.delivery.service.DeliveryService;
import com.coffeeproject.domain.order.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    }
}