package com.ecommerce.cart_service.service;

import com.ecommerce.cart_service.client.ProductClient;
import com.ecommerce.cart_service.dto.*;
import com.ecommerce.cart_service.model.CartItem;
import com.ecommerce.cart_service.model.DeliveryOption;
import com.ecommerce.cart_service.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartItemRepository cartItemRepository;
    private final ProductClient productClient;
    private final DeliveryOptionService deliveryOptionService;

    public List<CartItemResponse> getCartItems(boolean expandProduct) {
        return cartItemRepository.findAllByOrderByCreatedAtAsc().stream()
                .map(item -> toResponse(item, expandProduct))
                .toList();
    }

    public CartItemResponse addItem(AddCartItemRequest request) {
        if (request.productId() == null || request.productId().isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "Product id is required");
        }
        if (request.quantity() == null || request.quantity() < 1 || request.quantity() > 10) {
            throw new ResponseStatusException(BAD_REQUEST, "Quantity must be a number between 1 and 10");
        }
        requireProduct(request.productId());

        CartItem item = cartItemRepository.findById(request.productId())
                .map(existing -> {
                    existing.setQuantity(existing.getQuantity() + request.quantity());
                    return existing;
                })
                .orElseGet(() -> CartItem.builder()
                        .productId(request.productId())
                        .quantity(request.quantity())
                        .deliveryOptionId("1")
                        .createdAt(System.currentTimeMillis())
                        .build());

        return toResponse(cartItemRepository.save(item), false);
    }

    public CartItemResponse updateItem(String productId, UpdateCartItemRequest request) {
        CartItem item = cartItemRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Cart item not found"));

        if (request.quantity() != null) {
            if (request.quantity() < 1) {
                throw new ResponseStatusException(BAD_REQUEST, "Quantity must be a number greater than 0");
            }
            item.setQuantity(request.quantity());
        }
        if (request.deliveryOptionId() != null) {
            deliveryOptionService.require(request.deliveryOptionId());
            item.setDeliveryOptionId(request.deliveryOptionId());
        }
        return toResponse(cartItemRepository.save(item), false);
    }

    public void deleteItem(String productId) {
        if (!cartItemRepository.existsById(productId)) {
            throw new ResponseStatusException(NOT_FOUND, "Cart item not found");
        }
        cartItemRepository.deleteById(productId);
    }

    public PaymentSummaryResponse getPaymentSummary() {
        Totals totals = calculateTotals();
        return new PaymentSummaryResponse(
                totals.totalItems,
                totals.productCostCents,
                totals.shippingCostCents,
                totals.totalBeforeTax,
                totals.taxCents,
                totals.totalCostCents
        );
    }

    public CheckoutSnapshot getCheckoutSnapshot() {
        List<CheckoutSnapshot.Item> items = cartItemRepository.findAllByOrderByCreatedAtAsc().stream()
                .map(item -> {
                    requireProduct(item.getProductId());
                    DeliveryOption option = deliveryOptionService.require(item.getDeliveryOptionId());
                    return new CheckoutSnapshot.Item(
                            item.getProductId(),
                            item.getQuantity(),
                            deliveryOptionService.estimatedDeliveryTime(option)
                    );
                })
                .toList();
        return new CheckoutSnapshot(calculateTotals().totalCostCents, items);
    }

    public void clearCart() {
        cartItemRepository.deleteAll();
    }

    private Totals calculateTotals() {
        int totalItems = 0;
        int productCost = 0;
        int shippingCost = 0;
        for (CartItem item : cartItemRepository.findAllByOrderByCreatedAtAsc()) {
            ProductResponse product = requireProduct(item.getProductId());
            DeliveryOption option = deliveryOptionService.require(item.getDeliveryOptionId());
            totalItems += item.getQuantity();
            productCost += product.priceCents() * item.getQuantity();
            shippingCost += option.getPriceCents();
        }
        int beforeTax = productCost + shippingCost;
        int tax = (int) Math.round(beforeTax * 0.1);
        return new Totals(totalItems, productCost, shippingCost, beforeTax, tax, beforeTax + tax);
    }

    private ProductResponse requireProduct(String productId) {
        try {
            return productClient.getProduct(productId);
        } catch (RestClientResponseException exception) {
            if (exception.getStatusCode().is4xxClientError()) {
                throw new ResponseStatusException(BAD_REQUEST, "Product not found");
            }
            throw exception;
        }
    }

    private CartItemResponse toResponse(CartItem item, boolean expandProduct) {
        return new CartItemResponse(
                item.getProductId(),
                item.getQuantity(),
                item.getDeliveryOptionId(),
                expandProduct ? requireProduct(item.getProductId()) : null
        );
    }

    private record Totals(
            int totalItems,
            int productCostCents,
            int shippingCostCents,
            int totalBeforeTax,
            int taxCents,
            int totalCostCents
    ) {}
}
