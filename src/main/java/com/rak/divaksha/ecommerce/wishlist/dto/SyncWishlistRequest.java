package com.rak.divaksha.ecommerce.wishlist.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SyncWishlistRequest {
    @NotNull
    private List<Long> productIds;
}
