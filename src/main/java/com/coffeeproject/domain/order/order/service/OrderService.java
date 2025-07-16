package com.coffeeproject.domain.order.order.service;

import com.coffeeproject.domain.order.order.dto.OrderRequest;
import com.coffeeproject.domain.order.order.dto.OrderUpdateRequest;
import com.coffeeproject.domain.order.order.entity.Order;
import com.coffeeproject.domain.order.order.repository.OrderRepository;
import com.coffeeproject.domain.order.orderitem.OrderItem;
import com.coffeeproject.domain.order.orderitem.dto.OrderUpdateItemRequest;
import com.coffeeproject.domain.product.product.entity.Product;
import com.coffeeproject.domain.product.product.repository.ProductRepository;
import com.coffeeproject.global.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Order createOrder(OrderRequest request) {
        Order order = Order.createOrder(
                request.customerEmail(),
                request.shippingAddress(),
                request.shippingZipCode()
        );

        // 요청마다 상품을 조회하여 OrderItem 생성
        request.items().forEach(itemRequest -> {
            Product product = productRepository.findById(itemRequest.productId())
                    .orElseThrow(() -> new ServiceException("400", itemRequest.productId() + "번 상품이 존재하지 않습니다."));
            OrderItem orderItem = OrderItem.createOrderItem(product, order, itemRequest.quantity());
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

    @Transactional
    public void deleteOrder(int id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ServiceException("400", id + "번 주문이 존재하지 않습니다."));
        orderRepository.delete(order);
    }

    @Transactional
    public Order updateOrder(int id, OrderUpdateRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ServiceException("400", id + "번 주문이 존재하지 않습니다."));

        order.updateShippingInfo(request.shippingAddress(), request.shippingZipCode());
        order.updateStatus(request.status());

        // 현재 주문에 존재하는 OrderItem
        Map<Integer, OrderItem> existingItems = order.getOrderItems().stream()
                .collect(Collectors.toMap(
                        item -> item.getProduct().getId(), item -> item)
                );

        Set<Integer> updatedProductIds = new HashSet<>();

        for (OrderUpdateItemRequest itemRequest : request.items()) {
            int productId = itemRequest.productId();
            int quantity = itemRequest.quantity();

            updatedProductIds.add(productId);

            if (existingItems.containsKey(productId)) {
                existingItems.get(productId).updateQuantity(quantity);
            } else {
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new ServiceException("400", productId + "번 상품이 존재하지 않습니다."));

                OrderItem newItem = OrderItem.createOrderItem(product, order, quantity);
                order.addOrderItem(newItem);
            }
        }

        order.getOrderItems().removeIf(item ->
                !updatedProductIds.contains(item.getProduct().getId()));

        order.calculateTotalAmount();

        return order;
    }
}
