package com.ecommerce.order_service.client;

import com.ecommerce.order_service.dto.ProductResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

public interface ProductClient {
    @GetExchange("/api/products/{productId}")
    ProductResponse getProduct(@PathVariable String productId);
}
