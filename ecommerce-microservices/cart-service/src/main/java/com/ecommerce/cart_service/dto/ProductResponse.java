package com.ecommerce.cart_service.dto;

import java.util.List;

public record ProductResponse(
        String id,
        String image,
        String name,
        Rating rating,
        Integer priceCents,
        List<String> keywords
) {
    public record Rating(Double stars, Integer count) {}
}
