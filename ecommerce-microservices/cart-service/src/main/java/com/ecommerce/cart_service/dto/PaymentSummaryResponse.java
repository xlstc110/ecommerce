package com.ecommerce.cart_service.dto;

public record PaymentSummaryResponse(
        Integer totalItems,
        Integer productCostCents,
        Integer shippingCostCents,
        Integer totalCostBeforeTaxCents,
        Integer taxCents,
        Integer totalCostCents
) {}
