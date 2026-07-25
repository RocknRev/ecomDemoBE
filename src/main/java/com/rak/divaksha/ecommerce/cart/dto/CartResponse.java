package com.rak.divaksha.ecommerce.cart.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
public class CartResponse {

    private Long cartId;

    private Integer totalItems;

    private BigDecimal totalAmount;

    private List<CartItemResponse> items;

}