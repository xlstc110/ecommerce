package com.ecommerce.order_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    @Column(name = "product_id")
    private String productId;
    private Integer quantity;
    @Column(name = "estimated_delivery_time_ms")
    private Long estimatedDeliveryTimeMs;
}
