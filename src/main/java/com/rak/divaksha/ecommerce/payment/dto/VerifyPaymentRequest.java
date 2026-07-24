package com.rak.divaksha.ecommerce.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyPaymentRequest {

    @NotNull
    private Long orderId;

    @NotBlank
    private String gatewayOrderId;

    @NotBlank
    private String gatewayPaymentId;

    @NotBlank
    private String gatewaySignature;

}