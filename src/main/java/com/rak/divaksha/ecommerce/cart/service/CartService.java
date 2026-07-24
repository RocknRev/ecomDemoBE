package com.rak.divaksha.ecommerce.cart.service;


import com.rak.divaksha.ecommerce.cart.dto.AddToCartRequest;
import com.rak.divaksha.ecommerce.cart.dto.CartResponse;
import com.rak.divaksha.ecommerce.cart.dto.UpdateCartItemRequest;

public interface CartService {

    CartResponse getCart();

    CartResponse addToCart(AddToCartRequest request);

    CartResponse updateQuantity(Long cartItemId, UpdateCartItemRequest request);

    CartResponse removeItem(Long cartItemId);

    void clearCart();

}