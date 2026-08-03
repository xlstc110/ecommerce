package com.ecommerce.order_service.service;

import com.ecommerce.order_service.client.CartClient;
import com.ecommerce.order_service.client.InventoryClient;
import com.ecommerce.order_service.client.ProductClient;
import com.ecommerce.order_service.dto.CheckoutSnapshot;
import com.ecommerce.order_service.dto.OrderResponse;
import com.ecommerce.order_service.event.OrderPlacedEvent;
import com.ecommerce.order_service.mapper.OrderMapper;
import com.ecommerce.order_service.model.Order;
import com.ecommerce.order_service.model.OrderItem;
import com.ecommerce.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final CartClient cartClient;
    private final ProductClient productClient;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public List<OrderResponse> getOrders(String userId, boolean expandProducts) {
        return orderRepository.findAllByUserIdOrderByOrderTimeMsDesc(userId).stream()
                .map(order -> toResponse(order, expandProducts))
                .toList();
    }

    public OrderResponse getOrder(String userId, String orderId, boolean expandProducts) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        return toResponse(order, expandProducts);
    }

    @Transactional
    public OrderResponse placeOrder(String userId, String authorization) {
        CheckoutSnapshot snapshot = cartClient.getCheckoutSnapshot(authorization);
        if (snapshot.items() == null || snapshot.items().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty");
        }

        for (CheckoutSnapshot.Item item : snapshot.items()) {
            boolean inStock = inventoryClient.isInStock(item.productId(), item.quantity());
            if (!inStock) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Product is out of stock: " + item.productId()
                );
            }
        }

        Order order = Order.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .orderTimeMs(System.currentTimeMillis())
                .totalCostCents(snapshot.totalCostCents())
                .products(snapshot.items().stream()
                        .map(item -> OrderItem.builder()
                                .productId(item.productId())
                                .quantity(item.quantity())
                                .estimatedDeliveryTimeMs(item.estimatedDeliveryTimeMs())
                                .build())
                        .toList())
                .build();

        Order savedOrder = orderRepository.save(order);
        cartClient.clearCart(authorization);
        kafkaTemplate.send(
                "order-placed",
                new OrderPlacedEvent(savedOrder.getId(), userId)
        );
        return toResponse(savedOrder, false);
    }

    private OrderResponse toResponse(Order order, boolean expandProducts) {
        return OrderMapper.toResponse(order, expandProducts, productClient::getProduct);
    }
}
