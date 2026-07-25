package com.rak.divaksha.ecommerce.order.dto;

import com.rak.divaksha.ecommerce.common.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaceOrderRequest {

    @NotNull
    private Long addressId;

    @NotNull
    private PaymentMethod paymentMethod;

    private String notes;

}