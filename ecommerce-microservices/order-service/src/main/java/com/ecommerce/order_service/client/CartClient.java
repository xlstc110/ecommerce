package com.ecommerce.order_service.client;

import com.ecommerce.order_service.dto.CheckoutSnapshot;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;

public interface CartClient {
    @GetExchange("/internal/cart-snapshot")
    CheckoutSnapshot getCheckoutSnapshot();

    @DeleteExchange("/internal/cart-items")
    void clearCart();
}
