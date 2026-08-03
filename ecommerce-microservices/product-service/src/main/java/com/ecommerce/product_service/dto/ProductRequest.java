package com.ecommerce.product_service.dto;

import com.ecommerce.product_service.model.Product;

import java.util.List;

public record ProductRequest(
        String id,
        String image,
        String name,
        Product.Rating rating,
        Integer priceCents,
        List<String> keywords
) {}
