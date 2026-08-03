package com.ecommerce.cart_service.dto;

public record UpdateCartItemRequest(Integer quantity, String deliveryOptionId) {}
