package com.coffeeproject.domain.delivery.repository;

import com.coffeeproject.domain.delivery.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Integer> {
    List<Delivery> findByCustomerEmail(String customerEmail);
}
