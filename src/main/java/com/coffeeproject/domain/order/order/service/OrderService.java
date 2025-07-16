package com.coffeeproject.domain.order.order.service;

import com.coffeeproject.domain.order.order.dto.OrderRequest;
import com.coffeeproject.domain.order.order.entity.Order;
import com.coffeeproject.domain.order.order.repository.OrderRepository;
import com.coffeeproject.domain.order.orderitem.OrderItem;
import com.coffeeproject.domain.product.product.entity.Product;
import com.coffeeproject.domain.product.product.repository.ProductRepository;
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
}
