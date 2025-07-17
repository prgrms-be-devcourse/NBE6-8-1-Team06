package com.coffeeproject.domain.delivery.api;

import com.coffeeproject.domain.delivery.entity.Delivery;
import com.coffeeproject.domain.delivery.repository.DeliveryRepository;
import com.coffeeproject.domain.delivery.service.DeliveryService;
import com.coffeeproject.domain.order.order.entity.Order;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ApiV1DeliveryControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @BeforeEach
    void setUp() {
        deliveryRepository.deleteAll();

        // 테스트를 위한 초기 배송 데이터 생성
        Order order1 = Order.builder()
                .customerEmail("user1@example.com")
                .shippingAddress("서울시 강남구")
                .totalPrice(15000)
                .build();

        Order order2 = Order.builder()
                .customerEmail("user2@example.com")
                .shippingAddress("부산시 해운대구")
                .totalPrice(20000)
                .build();
        Order order3 = Order.builder()
                .customerEmail("user2@example.com")
                .shippingAddress("부산시 해운대구")
                .totalPrice(20000)
                .build();

        deliveryService.createDelivery(Arrays.asList(order1)); 
        deliveryService.createDelivery(Arrays.asList(order2, order3)); 
    }

    @Test
    @DisplayName("배송 정보 다건 조회")
    void t1() throws Exception {
        ResultActions resultActions = mvc
                .perform(get("/api/v1/delivery"))
                .andDo(print());

        List<Delivery> allDeliveries = deliveryService.getAllDeliveries(); 

        resultActions
                .andExpect(handler().handlerType(ApiV1DeliveryController.class))
                .andExpect(handler().methodName("deliveryInfo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.msg").value("배송 정보 조회 성공"))
                .andExpect(jsonPath("$.data.length()").value(allDeliveries.size()));

        for (int i = 0; i < allDeliveries.size(); i++) {
            Delivery delivery = allDeliveries.get(i);
            resultActions
                    .andExpect(jsonPath("$.data[%d].id".formatted(i)).value(delivery.getId()))
                    .andExpect(jsonPath("$.data[%d].customerEmail".formatted(i)).value(delivery.getCustomerEmail()))
                    .andExpect(jsonPath("$.data[%d].shippingAddress".formatted(i)).value(delivery.getShippingAddress()))
                    .andExpect(jsonPath("$.data[%d].status".formatted(i)).value(delivery.getStatus().name()))
                    .andExpect(jsonPath("$.data[%d].orderItems.length()".formatted(i)).value(delivery.getOrders().size()));
        }
    }

    @Test
    @DisplayName("배송 정보 단건 조회")
    void t2() throws Exception {
        Delivery existingDelivery = deliveryService.getAllDeliveries().getFirst();
        int deliveryId = existingDelivery.getId(); 

        ResultActions resultActions = mvc
                .perform(get("/api/v1/delivery/" + deliveryId))
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1DeliveryController.class))
                .andExpect(handler().methodName("deliveryInfoById"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.msg").value("배송 정보 단건 조회 성공"))
                .andExpect(jsonPath("$.data.id").value(deliveryId))
                .andExpect(jsonPath("$.data.customerEmail").value(existingDelivery.getCustomerEmail()))
                .andExpect(jsonPath("$.data.shippingAddress").value(existingDelivery.getShippingAddress()))
                .andExpect(jsonPath("$.data.status").value(existingDelivery.getStatus().name()));
    }
    @Test
    @DisplayName("배송 정보 수정")
    void t3() throws Exception {
        Delivery existingDelivery = deliveryService.getAllDeliveries().getFirst();
        int deliveryId = existingDelivery.getId();

        String newShippingAddress = "제주도 서귀포시";
        String newStatus = Delivery.DeliveryStatus.SHIPPED.name();

        ApiV1DeliveryController.DeliveryUpdateRequestBody requestBody =
                new ApiV1DeliveryController.DeliveryUpdateRequestBody(newShippingAddress, newStatus);

        ResultActions resultActions = mvc
                .perform(
                        put("/api/v1/delivery/" + deliveryId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestBody))
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1DeliveryController.class))
                .andExpect(handler().methodName("updateDelivery"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.msg").value("배송 정보 업데이트 성공"))
                .andExpect(jsonPath("$.data.id").value(deliveryId))
                .andExpect(jsonPath("$.data.shippingAddress").value(newShippingAddress))
                .andExpect(jsonPath("$.data.status").value(newStatus));

        Delivery updatedDeliveryInDb = deliveryService.getDeliveryById(deliveryId);
        assertThat(updatedDeliveryInDb.getShippingAddress()).isEqualTo(newShippingAddress);
        assertThat(updatedDeliveryInDb.getStatus()).isEqualTo(Delivery.DeliveryStatus.SHIPPED);
    }
    //TODO : 에러 헨들러 도입 후 예외 케이스에 대한 테스트 작성
}
