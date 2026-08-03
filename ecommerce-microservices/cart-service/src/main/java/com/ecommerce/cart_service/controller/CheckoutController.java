package com.ecommerce.cart_service.controller;

import com.ecommerce.cart_service.dto.CheckoutSnapshot;
import com.ecommerce.cart_service.dto.DeliveryOptionResponse;
import com.ecommerce.cart_service.dto.PaymentSummaryResponse;
import com.ecommerce.cart_service.config.DefaultDataInitializer;
import com.ecommerce.cart_service.service.CartService;
import com.ecommerce.cart_service.service.DeliveryOptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CheckoutController {
    private final DeliveryOptionService deliveryOptionService;
    private final CartService cartService;
    private final DefaultDataInitializer defaultDataInitializer;

    @GetMapping("/api/delivery-options")
    public List<DeliveryOptionResponse> getDeliveryOptions(
            @RequestParam(required = false) String expand) {
        return deliveryOptionService.getDeliveryOptions("estimatedDeliveryTime".equals(expand));
    }

    @GetMapping("/api/payment-summary")
    public PaymentSummaryResponse getPaymentSummary() {
        return cartService.getPaymentSummary();
    }

    @GetMapping("/internal/cart-snapshot")
    public CheckoutSnapshot getCheckoutSnapshot() {
        return cartService.getCheckoutSnapshot();
    }

    @DeleteMapping("/internal/cart-items")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearCart() {
        cartService.clearCart();
    }

    @PostMapping("/internal/reset")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reset() throws Exception {
        defaultDataInitializer.reset();
    }
}
