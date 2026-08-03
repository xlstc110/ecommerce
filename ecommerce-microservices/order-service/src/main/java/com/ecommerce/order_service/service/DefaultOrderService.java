package com.ecommerce.order_service.service;

import com.ecommerce.order_service.model.Order;
import com.ecommerce.order_service.model.OrderItem;
import com.ecommerce.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DefaultOrderService {
    static final String DEMO_USER_ID = "demo-user";

    private final OrderRepository orderRepository;

    @Transactional
    public void reset() {
        orderRepository.deleteAll();
        orderRepository.saveAll(List.of(
                Order.builder()
                        .id("27cba69d-4c3d-4098-b42d-ac7fa62b7664")
                        .userId(DEMO_USER_ID)
                        .orderTimeMs(1723456800000L)
                        .totalCostCents(3506)
                        .products(List.of(
                                item("e43638ce-6aa0-4b85-b27f-e1d07eb678c6", 1, 1723716000000L),
                                item("83d4ca15-0f35-48f5-b7a3-1ea210004f2e", 2, 1723456800000L)))
                        .build(),
                Order.builder()
                        .id("b6b6c212-d30e-4d4a-805d-90b52ce6b37d")
                        .userId(DEMO_USER_ID)
                        .orderTimeMs(1718013600000L)
                        .totalCostCents(4190)
                        .products(List.of(
                                item("15b6fc6f-327a-4ec4-896f-486349e85a3d", 2, 1718618400000L)))
                        .build()));
    }

    private OrderItem item(String productId, int quantity, long deliveryTimeMs) {
        return OrderItem.builder()
                .productId(productId)
                .quantity(quantity)
                .estimatedDeliveryTimeMs(deliveryTimeMs)
                .build();
    }
}
