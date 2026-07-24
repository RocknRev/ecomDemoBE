package com.rak.divaksha.ecommerce.payment.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreatePaymentResponse {

    private Long paymentId;

    private String gatewayOrderId;

    private String gatewayKey;

    private String currency;

    private String amount;

}