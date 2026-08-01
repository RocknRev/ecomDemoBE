package com.rak.divaksha.ecommerce.wishlist.service;

import com.rak.divaksha.ecommerce.wishlist.dto.WishlistResponse;

import java.util.List;

public interface WishlistService {
    WishlistResponse getWishlist();
    WishlistResponse addProduct(Long productId);
    WishlistResponse removeProduct(Long productId);
    WishlistResponse syncProducts(List<Long> productIds);
}
