package com.rak.divaksha.ecommerce.cart.service.impl;

import com.rak.divaksha.ecommerce.auth.entity.User;
import com.rak.divaksha.ecommerce.auth.repository.UserRepository;
import com.rak.divaksha.ecommerce.cart.dto.AddToCartRequest;
import com.rak.divaksha.ecommerce.cart.dto.UpdateCartItemRequest;
import com.rak.divaksha.ecommerce.cart.dto.CartItemResponse;
import com.rak.divaksha.ecommerce.cart.dto.CartResponse;
import com.rak.divaksha.ecommerce.cart.entity.Cart;
import com.rak.divaksha.ecommerce.cart.entity.CartItem;
import com.rak.divaksha.ecommerce.cart.repository.CartItemRepository;
import com.rak.divaksha.ecommerce.cart.repository.CartRepository;
import com.rak.divaksha.ecommerce.cart.service.CartService;
import com.rak.divaksha.ecommerce.exception.BadRequestException;
import com.rak.divaksha.ecommerce.exception.ResourceNotFoundException;
import com.rak.divaksha.ecommerce.product.entity.Product;
import com.rak.divaksha.ecommerce.product.repository.ProductRepository;
import com.rak.divaksha.ecommerce.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    private User currentUser() {

        CustomUserDetails user =
                (CustomUserDetails) SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getPrincipal();

        return userRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    }

    private Cart getOrCreateCart() {

        User user = currentUser();

        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUser(user);
                    return cartRepository.save(cart);
                });

    }

    @Override
    public CartResponse getCart() {

        Cart cart = getOrCreateCart();

        return map(cart);

    }

    @Override
    public CartResponse addToCart(AddToCartRequest request) {

        Cart cart = getOrCreateCart();

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found"));

        if (!Boolean.TRUE.equals(product.getActive())) {
            throw new BadRequestException("Product is inactive");
        }

        if (request.getQuantity() > product.getStock()) {
            throw new BadRequestException("Insufficient stock");
        }

        String flavor = request.getFlavor() == null ? "" : request.getFlavor().trim();
        if (!product.getFlavors().isEmpty() && (flavor.isBlank() || !product.getFlavors().contains(flavor))) {
            throw new BadRequestException("Please choose a valid flavor");
        }
        if (product.getFlavors().isEmpty() && !flavor.isBlank()) {
            throw new BadRequestException("This product does not have flavors");
        }

        CartItem item = cartItemRepository
                .findByCartIdAndProductIdAndFlavor(cart.getId(), product.getId(), flavor)
                .orElse(null);

        if (item == null) {

            item = new CartItem();
            item.setCart(cart);
            item.setProduct(product);
            item.setQuantity(request.getQuantity());
            item.setFlavor(flavor);

            cart.getItems().add(item);

        } else {

            int qty = item.getQuantity() + request.getQuantity();

            if (qty > product.getStock()) {
                throw new BadRequestException("Insufficient stock");
            }

            item.setQuantity(qty);

        }

        cartRepository.save(cart);

        return map(cart);

    }

    @Override
    public CartResponse updateQuantity(Long cartItemId,
                                       UpdateCartItemRequest request) {

        Cart cart = getOrCreateCart();

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cart item not found"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new ResourceNotFoundException("Cart item not found");
        }

        if (request.getQuantity() > item.getProduct().getStock()) {
            throw new BadRequestException("Insufficient stock");
        }

        item.setQuantity(request.getQuantity());

        cartItemRepository.save(item);

        return map(cart);

    }

    @Override
    public CartResponse removeItem(Long cartItemId) {

        Cart cart = getOrCreateCart();

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cart item not found"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new ResourceNotFoundException("Cart item not found");
        }

        cart.getItems().remove(item);

        cartItemRepository.delete(item);

        return map(cart);

    }

    @Override
    public void clearCart() {

        Cart cart = getOrCreateCart();

        cart.getItems().clear();

        cartRepository.save(cart);

    }

    private CartResponse map(Cart cart) {

        List<CartItemResponse> items = cart.getItems()
                .stream()
                .map(item -> {

                    BigDecimal price = item.getProduct().getDiscountPrice() != null
                            ? item.getProduct().getDiscountPrice()
                            : item.getProduct().getPrice();

                    return CartItemResponse.builder()
                            .cartItemId(item.getId())
                            .productId(item.getProduct().getId())
                            .productName(item.getProduct().getName())
                            .thumbnailUrl(item.getProduct().getFlavorImages().getOrDefault(item.getFlavor(), item.getProduct().getThumbnailUrl()))
                            .flavor(item.getFlavor())
                            .quantity(item.getQuantity())
                            .price(price)
                            .subtotal(price.multiply(BigDecimal.valueOf(item.getQuantity())))
                            .build();

                })
                .toList();

        BigDecimal totalAmount = items.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalItems = items.stream()
                .mapToInt(CartItemResponse::getQuantity)
                .sum();

        return CartResponse.builder()
                .cartId(cart.getId())
                .items(items)
                .totalItems(totalItems)
                .totalAmount(totalAmount)
                .build();

    }

}
