package com.coffeeproject.domain.delivery.entity;

import com.coffeeproject.global.jpa.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
//fixme : 교체될 Order엔티티도 테이블 명을 교체해야합니다.
@Table(name ="customer_order")
public class Order extends BaseEntity {
    //FIXME : 실제 Order 엔티티가 구현되면 삭제해야합니다.
    private String customerEmail;
    private String shippingAddress;
    private int totalPrice;
}
