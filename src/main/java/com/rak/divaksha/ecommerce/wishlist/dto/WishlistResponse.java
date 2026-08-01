package com.rak.divaksha.ecommerce.wishlist.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class WishlistResponse {
    private Long wishlistId;
    private List<Long> productIds;
}
