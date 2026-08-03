package com.ecommerce.order_service.client;

import com.ecommerce.order_service.dto.CheckoutSnapshot;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.bind.annotation.RequestHeader;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public interface CartClient {
    @GetExchange("/internal/cart-snapshot")
    CheckoutSnapshot getCheckoutSnapshot(@RequestHeader(AUTHORIZATION) String authorization);

    @DeleteExchange("/internal/cart-items")
    void clearCart(@RequestHeader(AUTHORIZATION) String authorization);
}
