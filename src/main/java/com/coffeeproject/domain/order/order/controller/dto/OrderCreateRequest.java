package com.coffeeproject.domain.order.order.controller.dto;

import com.coffeeproject.domain.order.order.service.dto.OrderCreateServiceRequest;
import com.coffeeproject.domain.order.orderitem.dto.OrderItemRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record OrderCreateRequest (
        @Email(message = "유효한 이메일 형식이어야 합니다.")
        @NotBlank(message = "이메일은 필수입니다.")
        String customerEmail,

        @NotBlank(message = "주소는 필수입니다.")
        String shippingAddress,

        @NotBlank(message = "우편번호는 필수입니다.")
        String shippingZipCode,

        @Valid
        @NotEmpty(message = "주문 상품이 1개 이상 필요합니다.")
        List<OrderItemRequest> items
) {
    public OrderCreateServiceRequest toServiceRequest() {
        return new OrderCreateServiceRequest(
                customerEmail,
                shippingAddress,
                shippingZipCode,
                items.stream()
                        .map(item -> new OrderCreateServiceRequest.OrderItemServiceRequest(item.productId(), item.quantity()))
                        .toList()
        );
    }
}
