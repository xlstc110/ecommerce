package com.ecommerce.order_service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

public record OrderResponse(
        String id,
        Long orderTimeMs,
        Integer totalCostCents,
        List<Item> products
) {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Item(
            String productId,
            Integer quantity,
            Long estimatedDeliveryTimeMs,
            ProductResponse product
    ) {}
}
