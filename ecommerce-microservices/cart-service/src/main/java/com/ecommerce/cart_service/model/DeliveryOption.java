package com.ecommerce.cart_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("deliveryOptions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryOption {
    @Id
    private String id;
    private Integer deliveryDays;
    private Integer priceCents;
}
