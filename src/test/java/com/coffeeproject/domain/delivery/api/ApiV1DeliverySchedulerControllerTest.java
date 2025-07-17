package com.coffeeproject.domain.delivery.api;

import com.coffeeproject.domain.delivery.entity.Delivery;
import com.coffeeproject.domain.delivery.repository.DeliveryRepository;
import com.coffeeproject.domain.delivery.service.DeliverySchedulerService;
import com.coffeeproject.domain.delivery.task.DeliveryBatchTask;
import com.coffeeproject.domain.order.order.entity.Order;
import com.coffeeproject.domain.order.order.enums.OrderStatus;
import com.coffeeproject.domain.order.order.repository.OrderRepository;
import com.coffeeproject.global.jpa.entity.BaseEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ApiV1DeliverySchedulerControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private DeliverySchedulerService deliverySchedulerService;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private DeliveryBatchTask deliveryBatchTask;

    @BeforeEach
    void setUp() throws Exception {
        deliveryRepository.deleteAll();
        orderRepository.deleteAll();

        Order order1 = Order.builder()
                .customerEmail("user1@example.com")
                .shippingAddress("서울시 강남구")
                .totalPrice(15000)
                .status(OrderStatus.PAID)
                .build();
        setCreateDate(order1, LocalDateTime.now());

        Order order2 = Order.builder()
                .customerEmail("user2@example.com")
                .shippingAddress("부산시 해운대구")
                .totalPrice(20000)
                .status(OrderStatus.PAID)
                .build();
        setCreateDate(order2, LocalDateTime.now());

        Order order3 = Order.builder()
                .customerEmail("user2@example.com")
                .shippingAddress("부산시 해운대구")
                .totalPrice(20000)
                .status(OrderStatus.PAID)
                .build();
        setCreateDate(order3, LocalDateTime.now());

        orderRepository.saveAll(Arrays.asList(order1, order2, order3));
    }

    private void setCreateDate(Order order, LocalDateTime date) throws Exception {
        Field createDateField = BaseEntity.class.getDeclaredField("createDate");
        createDateField.setAccessible(true);
        createDateField.set(order, date);
    }

    @Test
    @DisplayName("엔드포인트로 시작 요청시 기본값으로 실행되는지")
    void schedulerStartTest() throws Exception {
        ResultActions resultActions = mvc
                .perform(get("/api/v1/scheduler/start"))
                .andDo(print());

        resultActions
                .andExpect(status().isOk());

        assertThat(deliverySchedulerService.isSchedulerRunning()).isTrue();
    }

    @Test
    @DisplayName("엔드포인트로 시작 요청시 요청한 Cron표현식에 따라 실행되는지")
    void schedulerStartTestWithCronExpression() throws Exception {
        String cronExpression = "0 0/5 * * * ?";

        ResultActions resultActions = mvc
                .perform(get("/api/v1/scheduler/start")
                        .param("cronExpression", cronExpression))
                .andDo(print());

        resultActions
                .andExpect(status().isOk());

        assertThat(deliverySchedulerService.isSchedulerRunning()).isTrue();
    }

    @Test
    @DisplayName("엔드포인트로 종료 요청시 종료되는지")
    void schedulerStopTest() throws Exception {
        deliverySchedulerService.startScheduler("0 0 0 * * ?");
        assertThat(deliverySchedulerService.isSchedulerRunning()).isTrue();

        ResultActions resultActions = mvc
                .perform(get("/api/v1/scheduler/stop"))
                .andDo(print());

        resultActions
                .andExpect(status().isOk());

        assertThat(deliverySchedulerService.isSchedulerRunning()).isFalse();
    }

    @Test
    @DisplayName("배치로 딜리버리 엔티티 생기는지 총액 및 다른 필드는 정확하게 들어가는지 확인")
    void deliveryCreationTest() {
        deliveryBatchTask.run();

        List<Delivery> deliveries = deliveryRepository.findAll();
        assertThat(deliveries).hasSize(2);

        Delivery user1Delivery = deliveries.stream()
                .filter(d -> d.getCustomerEmail().equals("user1@example.com"))
                .findFirst()
                .orElse(null);

        assertThat(user1Delivery).isNotNull();
        assertThat(user1Delivery.getShippingAddress()).isEqualTo("서울시 강남구");
        assertThat(user1Delivery.getStatus()).isEqualTo(Delivery.DeliveryStatus.PENDING);
        assertThat(user1Delivery.getOrders()).hasSize(1);
        assertThat(user1Delivery.getOrders().getFirst().getTotalPrice()).isEqualTo(15000);

        Delivery user2Delivery = deliveries.stream()
                .filter(d -> d.getCustomerEmail().equals("user2@example.com"))
                .findFirst()
                .orElse(null);

        assertThat(user2Delivery).isNotNull();
        assertThat(user2Delivery.getShippingAddress()).isEqualTo("부산시 해운대구");
        assertThat(user2Delivery.getStatus()).isEqualTo(Delivery.DeliveryStatus.PENDING);
        assertThat(user2Delivery.getOrders()).hasSize(2);

        int totalForUser2 = user2Delivery.getOrders().stream().mapToInt(Order::getTotalPrice).sum();
        assertThat(totalForUser2).isEqualTo(40000);
    }
}