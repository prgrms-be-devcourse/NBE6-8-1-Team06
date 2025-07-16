package com.coffeeproject.domain.order.order.controller;

import com.coffeeproject.domain.order.order.controller.dto.OrderCreateRequest;
import com.coffeeproject.domain.order.order.entity.Order;
import com.coffeeproject.domain.order.order.service.OrderService;
import com.coffeeproject.domain.order.orderitem.OrderItem;
import com.coffeeproject.domain.order.orderitem.dto.OrderItemRequest;
import com.coffeeproject.domain.product.product.entity.Product;
import com.coffeeproject.domain.product.product.repository.ProductRepository;
import com.coffeeproject.global.exception.ServiceException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EntityManager em;

    private OrderCreateRequest request;
    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        product1 = productRepository.save(new Product("아메리카노", "커피1", 3000));
        product2 = productRepository.save(new Product("에스프레소", "커피2", 5000));

        request = new OrderCreateRequest(
                "test@example.com",
                "경기도 남양주시",
                "111-111",
                List.of(
                        new OrderItemRequest(product1.getId(), 2),
                        new OrderItemRequest(product2.getId(), 1)
                )
        );
    }

    @Test
    @DisplayName("주문이 정상적으로 생성되는지 확인한다.")
    void createOrder() throws Exception {
        mockMvc.perform(post("/orders")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(OrderController.class))
                .andExpect(jsonPath("$.data.orderId").exists())
                .andExpect(jsonPath("$.data.totalPrice").value(11000))
                .andExpect(jsonPath("$.data.items.length()").value(2))
                .andExpect(jsonPath("$.data.items[0].quantity").value(2))
                .andExpect(jsonPath("$.data.items[1].quantity").value(1));
    }

    @Test
    @DisplayName("주문 생성 시 잘못된 요청이 들어오면 예외가 발생한다.")
    void createOrderWithInvalidRequest() throws Exception {
        OrderCreateRequest request = new OrderCreateRequest(
                "notEmail",
                "경기도 남양주시",
                "111-111",
                List.of(
                        new OrderItemRequest(product1.getId(), 2),
                        new OrderItemRequest(product2.getId(), 1)
                )
        );

        mockMvc.perform(post("/orders")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("전체 주문 목록 조회")
    void getAllOrders() throws Exception {
        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200"))
                .andExpect(jsonPath("$.msg").value("주문 목록이 조회되었습니다."))
                .andExpect(jsonPath("$.data").isArray())
                .andDo(print());
    }

    @Test
    @DisplayName("단일 주문 조회 성공")
    void getSingleOrder() throws Exception {
        Order order = orderService.createOrder(request.toServiceRequest());
        mockMvc.perform(get("/orders/{id}", order.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200"))
                .andExpect(jsonPath("$.msg").value(order.getId() + "번 주문이 조회되었습니다."))
                .andExpect(jsonPath("$.data.orderId").value(order.getId()))
                .andExpect(jsonPath("$.data.customerEmail").exists())
                .andExpect(jsonPath("$.data.shippingAddress").exists())
                .andExpect(jsonPath("$.data.shippingZipCode").exists())
                .andExpect(jsonPath("$.data.totalPrice").value(11000))
                .andExpect(jsonPath("$.data.items").isArray());
    }

    @Test
    @DisplayName("존재하지 않는 주문 ID 조회 시 예외 반환")
    void getOrderWithInvalidId() {
        int invalidId = 9999;
        assertThatThrownBy(() -> orderService.getOrderById(invalidId))
                .isInstanceOf(ServiceException.class);
    }

    @Test
    @DisplayName("주문 삭제 시 연관된 주문 항목도 모두 삭제된다.")
    void deleteOrder() throws Exception {
        Order order = orderService.createOrder(request.toServiceRequest());
        int orderId = order.getId();

        int itemCountBefore = order.getOrderItems().size();
        assertThat(itemCountBefore).isGreaterThan(0);

        List<Integer> orderItemIds = order.getOrderItems().stream()
                .map(OrderItem::getId)
                .toList();

        mockMvc.perform(delete("/orders/{id}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200"))
                .andExpect(jsonPath("$.msg").value(orderId + "번 주문이 삭제되었습니다."));

        em.flush();
        em.clear();

        // 각 주문 항목이 삭제되었는지 확인
        for (int itemId : orderItemIds) {
            OrderItem deletedItem = em.find(OrderItem.class, itemId);
            assertThat(deletedItem).isNull();
        }
    }

    @Test
    @DisplayName("존재하지 않는 주문 ID 삭제 시 예외 반환")
    void deleteOrderWithInvalidId() throws Exception {
        int invalidId = 9999;

        mockMvc.perform(delete("/orders/{id}", invalidId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("400"))
                .andExpect(jsonPath("$.msg").value(invalidId + "번 주문이 존재하지 않습니다."))
                .andDo(print());
    }
}