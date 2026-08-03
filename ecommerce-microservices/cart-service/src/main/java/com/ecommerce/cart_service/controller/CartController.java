package com.ecommerce.cart_service.controller;

import com.ecommerce.cart_service.dto.AddCartItemRequest;
import com.ecommerce.cart_service.dto.CartItemResponse;
import com.ecommerce.cart_service.dto.UpdateCartItemRequest;
import com.ecommerce.cart_service.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart-items")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping
    public List<CartItemResponse> getCartItems(
            @RequestParam(required = false) String expand) {
        return cartService.getCartItems("product".equals(expand));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CartItemResponse addItem(@RequestBody AddCartItemRequest request) {
        return cartService.addItem(request);
    }

    @PutMapping("/{productId}")
    public CartItemResponse updateItem(
            @PathVariable String productId,
            @RequestBody UpdateCartItemRequest request) {
        return cartService.updateItem(productId, request);
    }

    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(@PathVariable String productId) {
        cartService.deleteItem(productId);
    }
}
