package com.coffeeproject.domain.order.order.controller;

import com.coffeeproject.domain.order.order.controller.dto.OrderCreateRequest;
import com.coffeeproject.domain.order.order.controller.dto.OrderUpdateRequest;
import com.coffeeproject.domain.order.order.dto.OrderResponse;
import com.coffeeproject.domain.order.order.entity.Order;
import com.coffeeproject.domain.order.order.service.OrderService;
import com.coffeeproject.global.rsData.RsData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class ApiV1OrderController {
    private final OrderService orderService;

    @PostMapping
    public RsData<OrderResponse> createOrder(@Valid @RequestBody OrderCreateRequest request) {
        Order order = orderService.createOrder(request.toServiceRequest());
        return new RsData<>(
                "200",
                "%d번 주문이 생성되었습니다.".formatted(order.getId()),
                new OrderResponse(order)
        );
    }

    @GetMapping
    public RsData<List<OrderResponse>> getOrders() {
        List<Order> orders = orderService.getAllOrders();
        List<OrderResponse> orderResponses = orders.stream().map(OrderResponse::new).toList();
        return new RsData<>("200",
                "주문 목록이 조회되었습니다.",
                orderResponses
        );
    }

    @GetMapping("/{id}")
    public RsData<OrderResponse> getOrder(@PathVariable(value = "id") int id) {
        Order order = orderService.getOrderById(id);
        return new RsData<>("200",
                "%d번 주문이 조회되었습니다.".formatted(id),
                new OrderResponse(order)
        );
    }

    @DeleteMapping("/{id}")
    public RsData<Void> deleteOrder(@PathVariable(value = "id") int id) {
        orderService.deleteOrder(id);
        return new RsData<>("200",
                "%d번 주문이 삭제되었습니다.".formatted(id),
                null
        );
    }

    @PutMapping("/{id}")
    public RsData<OrderResponse> updateOrder(
            @PathVariable(value = "id") int id,
            @Valid @RequestBody OrderUpdateRequest request) {

        Order order = orderService.updateOrder(id, request.toServiceRequest());
        return new RsData<>("200",
                "%d번 주문이 수정되었습니다.".formatted(id),
                new OrderResponse(order)
        );
    }
}