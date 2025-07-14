package com.coffeeproject.domain.order.order.controller;

import com.coffeeproject.domain.order.order.dto.OrderRequest;
import com.coffeeproject.domain.order.order.entity.Order;
import com.coffeeproject.domain.order.order.service.OrderService;
import com.coffeeproject.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {
     private final OrderService orderService;

     @PostMapping
     public RsData<Void> createOrder(@RequestBody OrderRequest request) {
         Order order = orderService.createOrder(request);
         return new RsData<>(
                 "200",
                 "%d번 주문이 생성되었습니다.".formatted(order.getId())
         );
     }
}
