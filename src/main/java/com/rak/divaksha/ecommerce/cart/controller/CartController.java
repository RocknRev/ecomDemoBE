package com.rak.divaksha.ecommerce.cart.controller;

import com.rak.divaksha.ecommerce.cart.dto.AddToCartRequest;
import com.rak.divaksha.ecommerce.cart.dto.UpdateCartItemRequest;
import com.rak.divaksha.ecommerce.cart.dto.CartResponse;
import com.rak.divaksha.ecommerce.cart.service.CartService;
import com.rak.divaksha.ecommerce.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ApiResponse<CartResponse> getCart() {

        return ApiResponse.<CartResponse>builder()
                .success(true)
                .message("Cart fetched successfully")
                .data(cartService.getCart())
                .build();

    }

    @PostMapping("/items")
    public ApiResponse<CartResponse> addToCart(
            @Valid @RequestBody AddToCartRequest request) {

        return ApiResponse.<CartResponse>builder()
                .success(true)
                .message("Item added to cart")
                .data(cartService.addToCart(request))
                .build();

    }

    @PutMapping("/items/{cartItemId}")
    public ApiResponse<CartResponse> updateQuantity(
            @PathVariable Long cartItemId,
            @Valid @RequestBody UpdateCartItemRequest request) {

        return ApiResponse.<CartResponse>builder()
                .success(true)
                .message("Cart updated successfully")
                .data(cartService.updateQuantity(cartItemId, request))
                .build();

    }

    @DeleteMapping("/items/{cartItemId}")
    public ApiResponse<CartResponse> removeItem(
            @PathVariable Long cartItemId) {

        return ApiResponse.<CartResponse>builder()
                .success(true)
                .message("Item removed from cart")
                .data(cartService.removeItem(cartItemId))
                .build();

    }

    @DeleteMapping
    public ApiResponse<Void> clearCart() {

        cartService.clearCart();

        return ApiResponse.<Void>builder()
                .success(true)
                .message("Cart cleared successfully")
                .build();

    }

}