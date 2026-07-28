package com.rak.divaksha.ecommerce.cart.repository;

import com.rak.divaksha.ecommerce.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByCartIdAndProductIdAndFlavor(Long cartId, Long productId, String flavor);

}
