package com.ecommerce.cart_service.service;

import com.ecommerce.cart_service.client.ProductClient;
import com.ecommerce.cart_service.dto.*;
import com.ecommerce.cart_service.model.Cart;
import com.ecommerce.cart_service.model.CartItem;
import com.ecommerce.cart_service.model.DeliveryOption;
import com.ecommerce.cart_service.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final ProductClient productClient;
    private final DeliveryOptionService deliveryOptionService;

    public List<CartItemResponse> getCartItems(String userId, boolean expandProduct) {
        return getItems(userId).stream()
                .map(item -> toResponse(item, expandProduct))
                .toList();
    }

    public CartItemResponse addItem(String userId, AddCartItemRequest request) {
        if (request.productId() == null || request.productId().isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "Product id is required");
        }
        if (request.quantity() == null || request.quantity() < 1 || request.quantity() > 10) {
            throw new ResponseStatusException(BAD_REQUEST, "Quantity must be a number between 1 and 10");
        }
        requireProduct(request.productId());

        Cart cart = getOrCreateCart(userId);
        CartItem item = cart.getItems().stream()
                .filter(existing -> existing.getProductId().equals(request.productId()))
                .findFirst()
                .orElse(null);

        if (item == null) {
            item = CartItem.builder()
                    .productId(request.productId())
                    .quantity(request.quantity())
                    .deliveryOptionId("1")
                    .createdAt(System.currentTimeMillis())
                    .build();
            cart.getItems().add(item);
        } else {
            int newQuantity = item.getQuantity() + request.quantity();
            if (newQuantity > 10) {
                throw new ResponseStatusException(BAD_REQUEST, "Total quantity must not exceed 10");
            }
            item.setQuantity(newQuantity);
        }

        touchAndSave(cart);
        return toResponse(item, false);
    }

    public CartItemResponse updateItem(String userId, String productId, UpdateCartItemRequest request) {
        Cart cart = cartRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Cart item not found"));
        CartItem item = cart.getItems().stream()
                .filter(existing -> existing.getProductId().equals(productId))
                .findFirst()
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
        touchAndSave(cart);
        return toResponse(item, false);
    }

    public void deleteItem(String userId, String productId) {
        Cart cart = cartRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Cart item not found"));
        boolean removed = cart.getItems().removeIf(item -> item.getProductId().equals(productId));
        if (!removed) {
            throw new ResponseStatusException(NOT_FOUND, "Cart item not found");
        }
        touchAndSave(cart);
    }

    public PaymentSummaryResponse getPaymentSummary(String userId) {
        Totals totals = calculateTotals(userId);
        return new PaymentSummaryResponse(
                totals.totalItems,
                totals.productCostCents,
                totals.shippingCostCents,
                totals.totalBeforeTax,
                totals.taxCents,
                totals.totalCostCents
        );
    }

    public CheckoutSnapshot getCheckoutSnapshot(String userId) {
        List<CheckoutSnapshot.Item> items = getItems(userId).stream()
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
        return new CheckoutSnapshot(calculateTotals(userId).totalCostCents, items);
    }

    public void clearCart(String userId) {
        cartRepository.deleteById(userId);
    }

    private Totals calculateTotals(String userId) {
        int totalItems = 0;
        int productCost = 0;
        int shippingCost = 0;
        for (CartItem item : getItems(userId)) {
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

    private Cart getOrCreateCart(String userId) {
        return cartRepository.findById(userId)
                .orElseGet(() -> Cart.builder().userId(userId).build());
    }

    private List<CartItem> getItems(String userId) {
        return cartRepository.findById(userId)
                .map(Cart::getItems)
                .orElseGet(List::of)
                .stream()
                .sorted(Comparator.comparing(CartItem::getCreatedAt))
                .toList();
    }

    private void touchAndSave(Cart cart) {
        if (cart.getItems() == null) {
            cart.setItems(new ArrayList<>());
        }
        cart.setUpdatedAt(System.currentTimeMillis());
        cartRepository.save(cart);
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
