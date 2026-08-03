package com.ecommerce.cart_service.controller;

import com.ecommerce.cart_service.dto.AddCartItemRequest;
import com.ecommerce.cart_service.dto.CartItemResponse;
import com.ecommerce.cart_service.dto.UpdateCartItemRequest;
import com.ecommerce.cart_service.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart-items")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping
    public List<CartItemResponse> getCartItems(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) String expand) {
        return cartService.getCartItems(jwt.getSubject(), "product".equals(expand));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CartItemResponse addItem(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody AddCartItemRequest request) {
        return cartService.addItem(jwt.getSubject(), request);
    }

    @PutMapping("/{productId}")
    public CartItemResponse updateItem(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String productId,
            @RequestBody UpdateCartItemRequest request) {
        return cartService.updateItem(jwt.getSubject(), productId, request);
    }

    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String productId) {
        cartService.deleteItem(jwt.getSubject(), productId);
    }
}
