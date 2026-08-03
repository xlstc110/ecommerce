package com.ecommerce.order_service.dto;

import java.util.List;

public record CheckoutSnapshot(
        Integer totalCostCents,
        List<Item> items
) {
    public record Item(String productId, Integer quantity, Long estimatedDeliveryTimeMs) {}
}
