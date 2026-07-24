package com.rak.divaksha.ecommerce.payment.dto;

import com.rak.divaksha.ecommerce.common.enums.PaymentGateway;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePaymentRequest {

    @NotNull
    private Long orderId;

    @NotNull
    private PaymentGateway gateway;

}