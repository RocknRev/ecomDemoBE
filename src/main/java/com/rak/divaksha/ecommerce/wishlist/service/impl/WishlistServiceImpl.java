package com.rak.divaksha.ecommerce.wishlist.service.impl;

import com.rak.divaksha.ecommerce.auth.entity.User;
import com.rak.divaksha.ecommerce.auth.repository.UserRepository;
import com.rak.divaksha.ecommerce.exception.ResourceNotFoundException;
import com.rak.divaksha.ecommerce.product.entity.Product;
import com.rak.divaksha.ecommerce.product.repository.ProductRepository;
import com.rak.divaksha.ecommerce.security.CustomUserDetails;
import com.rak.divaksha.ecommerce.wishlist.dto.WishlistResponse;
import com.rak.divaksha.ecommerce.wishlist.entity.Wishlist;
import com.rak.divaksha.ecommerce.wishlist.entity.WishlistItem;
import com.rak.divaksha.ecommerce.wishlist.repository.WishlistRepository;
import com.rak.divaksha.ecommerce.wishlist.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class WishlistServiceImpl implements WishlistService {
    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public WishlistResponse getWishlist() {
        return map(getOrCreateWishlist());
    }

    @Override
    public WishlistResponse addProduct(Long productId) {
        Wishlist wishlist = getOrCreateWishlist();
        Product product = activeProduct(productId);
        boolean alreadySaved = wishlist.getItems().stream().anyMatch(item -> item.getProduct().getId().equals(product.getId()));
        if (!alreadySaved) {
            WishlistItem item = new WishlistItem();
            item.setWishlist(wishlist);
            item.setProduct(product);
            wishlist.getItems().add(item);
            wishlistRepository.save(wishlist);
        }
        return map(wishlist);
    }

    @Override
    public WishlistResponse removeProduct(Long productId) {
        Wishlist wishlist = getOrCreateWishlist();
        wishlist.getItems().removeIf(item -> item.getProduct().getId().equals(productId));
        wishlistRepository.save(wishlist);
        return map(wishlist);
    }

    @Override
    public WishlistResponse syncProducts(List<Long> productIds) {
        Wishlist wishlist = getOrCreateWishlist();
        List<Long> uniqueIds = new LinkedHashSet<>(productIds).stream().toList();
        List<Product> products = productRepository.findAllById(uniqueIds).stream()
                .filter(product -> Boolean.TRUE.equals(product.getActive()))
                .toList();
        wishlist.getItems().clear();
        for (Product product : products) {
            WishlistItem item = new WishlistItem();
            item.setWishlist(wishlist);
            item.setProduct(product);
            wishlist.getItems().add(item);
        }
        wishlistRepository.save(wishlist);
        return map(wishlist);
    }

    private Wishlist getOrCreateWishlist() {
        User user = currentUser();
        return wishlistRepository.findByUserId(user.getId()).orElseGet(() -> {
            Wishlist wishlist = new Wishlist();
            wishlist.setUser(user);
            return wishlistRepository.save(wishlist);
        });
    }

    private User currentUser() {
        CustomUserDetails principal = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findById(principal.getId()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private Product activeProduct(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        if (!Boolean.TRUE.equals(product.getActive())) throw new ResourceNotFoundException("Product not found");
        return product;
    }

    private WishlistResponse map(Wishlist wishlist) {
        return WishlistResponse.builder()
                .wishlistId(wishlist.getId())
                .productIds(wishlist.getItems().stream()
                        .map(WishlistItem::getProduct)
                        .filter(product -> Boolean.TRUE.equals(product.getActive()))
                        .map(Product::getId)
                        .toList())
                .build();
    }
}
