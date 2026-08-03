package com.ecommerce.order_service.controller;

import com.ecommerce.order_service.dto.OrderResponse;
import com.ecommerce.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/orders", "/api/order"})
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public List<OrderResponse> getOrders(@RequestParam(required = false) String expand) {
        return orderService.getOrders("products".equals(expand));
    }

    @GetMapping("/{orderId}")
    public OrderResponse getOrder(
            @PathVariable String orderId,
            @RequestParam(required = false) String expand) {
        return orderService.getOrder(orderId, "products".equals(expand));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder() {
        return orderService.placeOrder();
    }
}
