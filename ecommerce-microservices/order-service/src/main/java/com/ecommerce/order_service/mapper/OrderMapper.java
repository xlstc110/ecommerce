package com.ecommerce.order_service.mapper;

import com.ecommerce.order_service.dto.OrderRequest;
import com.ecommerce.order_service.dto.OrderResponse;
import com.ecommerce.order_service.model.Order;

import java.util.UUID;

public class OrderMapper {
    public static Order orderRequestToOrder(OrderRequest orderRequest) {
        return Order.builder()
                .orderNumber(UUID.randomUUID().toString())
                .price(orderRequest.price())
                .skuCode(orderRequest.skuCode())
                .quantity(orderRequest.quantity())
                .build();
    }

    public static OrderResponse orderToOrderResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .price(order.getPrice())
                .skuCode(order.getSkuCode())
                .quantity(order.getQuantity())
                .build();
    }
}
