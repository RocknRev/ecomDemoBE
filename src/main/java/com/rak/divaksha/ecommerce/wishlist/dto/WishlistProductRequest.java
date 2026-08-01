package com.rak.divaksha.ecommerce.wishlist.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WishlistProductRequest {
    @NotNull
    private Long productId;
}
