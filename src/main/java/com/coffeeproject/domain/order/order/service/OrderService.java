package com.coffeeproject.domain.order.order.service;

import com.coffeeproject.domain.order.order.entity.Order;
import com.coffeeproject.domain.order.order.enums.OrderStatus;
import com.coffeeproject.domain.order.order.repository.OrderRepository;
import com.coffeeproject.domain.order.order.service.dto.OrderCreateServiceRequest;
import com.coffeeproject.domain.order.order.service.dto.OrderUpdateServiceRequest;
import com.coffeeproject.domain.order.orderitem.OrderItem;
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

import static com.coffeeproject.domain.order.order.service.dto.OrderUpdateServiceRequest.*;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Order createOrder(OrderCreateServiceRequest request) {
        Order order = Order.createOrder(
                request.customerEmail(),
                request.shippingAddress(),
                request.shippingZipCode()
        );

        addOrderItems(order, request.items());

        order.calculateTotalAmount();

        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(int id) {
        return findOrderById(id);
    }

    @Transactional
    public void deleteOrder(int id) {
        Order order = findOrderById(id);
        orderRepository.delete(order);
    }

    @Transactional
    public Order updateOrder(int id, OrderUpdateServiceRequest request) {
        Order order = findOrderById(id);

        updateOrderBasicInfo(order, request);
        updateOrderItems(order, request);

        order.calculateTotalAmount();

        return order;
    }

    @Transactional
    public Order completePayment(int orderId) {
        Order order = findOrderById(orderId);
        order.paid();
        return orderRepository.save(order);
    }

    @Transactional
    public Order cancelPayment(int orderId) {
        Order order = findOrderById(orderId);
        order.cancel();
        return orderRepository.save(order);
    }

    private void addOrderItems(Order order, List<OrderCreateServiceRequest.OrderItemServiceRequest> itemRequests) {
        itemRequests.forEach(itemRequest -> {
            Product product = findProductById(itemRequest.productId());
            OrderItem orderItem = OrderItem.createOrderItem(product, order, itemRequest.quantity());
            order.addOrderItem(orderItem);
        });
    }

    private void updateOrderBasicInfo(Order order, OrderUpdateServiceRequest request) {
        order.updateShippingInfo(request.shippingAddress(), request.shippingZipCode());
        order.updateStatus(request.status());
    }

    private void updateOrderItems(Order order, OrderUpdateServiceRequest request) {
        // 현재 주문에 존재하는 OrderItem
        Map<Integer, OrderItem> existingItems = order.getOrderItems().stream()
                .collect(Collectors.toMap(
                        item -> item.getProduct().getId(), item -> item)
                );

        // 수정할 상품 ID를 저장하는 Set
        Set<Integer> updatedProductIds = new HashSet<>();

        for (OrderUpdateItemServiceRequest itemRequest : request.items()) {
            int productId = itemRequest.productId();
            int quantity = itemRequest.quantity();

            updatedProductIds.add(productId);

            // 이미 존재하는 상품이면 수량 업데이트
            if (existingItems.containsKey(productId)) {
                existingItems.get(productId).updateQuantity(quantity);
            }
            // 새로운 상품이면 OrderItem 생성 후 추가
            else {
                Product product = findProductById(productId);
                OrderItem newItem = OrderItem.createOrderItem(product, order, quantity);
                order.addOrderItem(newItem);
            }
        }

        // 기존 항목 중 수정되지 않은 항목은 삭제
        order.getOrderItems().removeIf(item ->
                !updatedProductIds.contains(item.getProduct().getId()));
    }

    private Order findOrderById(int id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ServiceException("400", id + "번 주문이 존재하지 않습니다."));
    }

    private Product findProductById(int productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ServiceException("400", productId + "번 상품이 존재하지 않습니다."));
    }
}