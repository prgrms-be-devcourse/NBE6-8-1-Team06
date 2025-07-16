package com.coffeeproject.domain.order.order.dto;

import com.coffeeproject.domain.order.order.enums.OrderStatus;
import com.coffeeproject.domain.order.orderitem.dto.OrderUpdateItemRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrderUpdateRequest(
        @NotBlank(message = "배송 주소는 필수입니다.")
        String shippingAddress,
        @NotBlank(message = "우편번호는 필수입니다.")
        String shippingZipCode,
        @NotNull(message = "주문 상태는 필수입니다.")
        OrderStatus status,
        @Valid
        @NotEmpty(message = "주문 상품이 1개 이상 필요합니다.")
        List<OrderUpdateItemRequest> items
) {
}
