package com.ecommerce.cart_service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record DeliveryOptionResponse(
        String id,
        Integer deliveryDays,
        Integer priceCents,
        Long estimatedDeliveryTimeMs
) {}
