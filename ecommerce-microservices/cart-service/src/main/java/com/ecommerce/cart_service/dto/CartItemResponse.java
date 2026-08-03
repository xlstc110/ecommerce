package com.ecommerce.cart_service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CartItemResponse(
        String productId,
        Integer quantity,
        String deliveryOptionId,
        ProductResponse product
) {}
