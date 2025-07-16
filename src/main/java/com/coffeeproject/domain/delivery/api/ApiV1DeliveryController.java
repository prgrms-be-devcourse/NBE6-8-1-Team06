package com.coffeeproject.domain.delivery.api;

import com.coffeeproject.domain.delivery.dto.DeliveryDto;
import com.coffeeproject.domain.delivery.entity.Delivery;
import com.coffeeproject.domain.delivery.service.DeliveryService;
import com.coffeeproject.global.rsData.RsData;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/delivery")
@RequiredArgsConstructor
@Tag(name = "ApiV1DeliveryController", description = "관리자용 API 배송조회 컨트롤러")
public class ApiV1DeliveryController {
    private final DeliveryService deliveryService;
    @Transactional
    @GetMapping
    public RsData<List<DeliveryDto>> deliveryInfo() {
        List<Delivery> allDeliveries = deliveryService.getAllDeliveries();
        return new RsData<>(
                "200-1",
                "배송 정보 조회 성공",
                allDeliveries.stream().map(DeliveryDto::new).toList()
        );
    }

    @Transactional
    @GetMapping("/{id}")
    public RsData<DeliveryDto> deliveryInfoById(@PathVariable(value = "id") int id) {
        Delivery delivery = deliveryService.getDeliveryById(id);
        return new RsData<>(
                "200-1",
                "배송 정보 단건 조회 성공",
                new DeliveryDto(delivery)
        );
    }

    record DeliveryUpdateRequestBody(
            @NotBlank String shippingAddress,
            @NotBlank String status
    ) {
    }

    @Transactional
    @PutMapping("/{id}")
    public RsData<DeliveryDto> updateDelivery(@PathVariable(value = "id") int id, @Valid @RequestBody DeliveryUpdateRequestBody request) {
        Delivery delivery = deliveryService.getDeliveryById(id);
        delivery.updateShippingAddress(request.shippingAddress());
        delivery.updateStatus(Delivery.DeliveryStatus.valueOf(request.status()));
        deliveryService.updateDelivery(id, delivery);
        return new RsData<>(
                "200-1",
                "배송 정보 업데이트 성공",
                new DeliveryDto(delivery)
        );
    }
}
