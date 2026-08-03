package com.ecommerce.api_gateway.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
public class ResetController {
    private final RestClient productClient;
    private final RestClient orderClient;
    private final RestClient cartClient;

    public ResetController(
            RestClient.Builder builder,
            @Value("${services.product.url}") String productUrl,
            @Value("${services.order.url}") String orderUrl,
            @Value("${services.cart.url}") String cartUrl) {
        productClient = builder.clone().baseUrl(productUrl).build();
        orderClient = builder.clone().baseUrl(orderUrl).build();
        cartClient = builder.clone().baseUrl(cartUrl).build();
    }

    @PostMapping("/api/reset")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reset() {
        productClient.post().uri("/internal/reset").retrieve().toBodilessEntity();
        cartClient.post().uri("/internal/reset").retrieve().toBodilessEntity();
        orderClient.post().uri("/internal/reset").retrieve().toBodilessEntity();
    }
}
