package com.rak.divaksha.ecommerce.wishlist.controller;

import com.rak.divaksha.ecommerce.common.dto.ApiResponse;
import com.rak.divaksha.ecommerce.wishlist.dto.SyncWishlistRequest;
import com.rak.divaksha.ecommerce.wishlist.dto.WishlistProductRequest;
import com.rak.divaksha.ecommerce.wishlist.dto.WishlistResponse;
import com.rak.divaksha.ecommerce.wishlist.service.WishlistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {
    private final WishlistService wishlistService;

    @GetMapping
    public ApiResponse<WishlistResponse> getWishlist() {
        return response("Wishlist fetched successfully", wishlistService.getWishlist());
    }

    @PostMapping("/items")
    public ApiResponse<WishlistResponse> addProduct(@Valid @RequestBody WishlistProductRequest request) {
        return response("Product added to wishlist", wishlistService.addProduct(request.getProductId()));
    }

    @DeleteMapping("/items/{productId}")
    public ApiResponse<WishlistResponse> removeProduct(@PathVariable Long productId) {
        return response("Product removed from wishlist", wishlistService.removeProduct(productId));
    }

    @PutMapping
    public ApiResponse<WishlistResponse> syncWishlist(@Valid @RequestBody SyncWishlistRequest request) {
        return response("Wishlist updated successfully", wishlistService.syncProducts(request.getProductIds()));
    }

    private ApiResponse<WishlistResponse> response(String message, WishlistResponse wishlist) {
        return ApiResponse.<WishlistResponse>builder().success(true).message(message).data(wishlist).build();
    }
}
