package com.rak.divaksha.ecommerce.cart.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class CartItemResponse {

    private Long cartItemId;

    private Long productId;

    private String productName;

    private String thumbnailUrl;

    private String flavor;

    private Integer quantity;

    private BigDecimal price;

    private BigDecimal subtotal;

}
