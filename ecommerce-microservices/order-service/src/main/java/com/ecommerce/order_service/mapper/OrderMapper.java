package com.ecommerce.order_service.mapper;

import com.ecommerce.order_service.dto.OrderResponse;
import com.ecommerce.order_service.dto.ProductResponse;
import com.ecommerce.order_service.model.Order;
import com.ecommerce.order_service.model.OrderItem;

import java.util.function.Function;

public final class OrderMapper {
    private OrderMapper() {}

    public static OrderResponse toResponse(
            Order order,
            boolean expandProducts,
            Function<String, ProductResponse> productLoader) {
        return new OrderResponse(
                order.getId(),
                order.getOrderTimeMs(),
                order.getTotalCostCents(),
                order.getProducts().stream()
                        .map(item -> toItem(item, expandProducts, productLoader))
                        .toList()
        );
    }

    private static OrderResponse.Item toItem(
            OrderItem item,
            boolean expandProducts,
            Function<String, ProductResponse> productLoader) {
        return new OrderResponse.Item(
                item.getProductId(),
                item.getQuantity(),
                item.getEstimatedDeliveryTimeMs(),
                expandProducts ? productLoader.apply(item.getProductId()) : null
        );
    }
}
