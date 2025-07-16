package com.coffeeproject.domain.delivery.service;

import com.coffeeproject.domain.delivery.entity.Delivery;
import com.coffeeproject.domain.delivery.entity.Order;
import com.coffeeproject.domain.delivery.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;

    public Delivery createDelivery(List<Order> orderList) {
        return deliveryRepository.save(
                Delivery.builder()
                        .customerEmail(orderList.getFirst().getCustomerEmail())
                        .shippingAddress(orderList.getFirst().getShippingAddress())
                        .orders(new ArrayList<>(orderList))// Delivery 엔티티의 필드에서 기본값을 제거후 새로 할당 하여 테스트가 통과됨
                        .totalPrice(orderList.stream().mapToInt(Order::getTotalPrice).sum())
                        .status(Delivery.DeliveryStatus.PENDING)
                        .build()
        );
    }
    public List<Delivery> getAllDeliveries() {
        return deliveryRepository.findAll();
    }
    public Delivery getDeliveryById(int id) {
        return deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery not found with id: " + id));
    }
    public List<Delivery> getDeliveryByEmail(String email) {
        return deliveryRepository.findByCustomerEmail(email);
    }
    public void updateDelivery(int id, Delivery updatedDelivery) {
        Delivery existingDelivery = getDeliveryById(id);
        existingDelivery.updateShippingAddress(updatedDelivery.getShippingAddress());
        existingDelivery.updateTotalPrice(updatedDelivery.getTotalPrice());
        existingDelivery.updateStatus(updatedDelivery.getStatus());
        deliveryRepository.save(existingDelivery);
    }
}
