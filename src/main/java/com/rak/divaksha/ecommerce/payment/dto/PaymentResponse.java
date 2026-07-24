package com.rak.divaksha.ecommerce.payment.dto;

import com.rak.divaksha.ecommerce.common.enums.PaymentGateway;
import com.rak.divaksha.ecommerce.common.enums.PaymentStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class PaymentResponse {

    private Long paymentId;

    private Long orderId;

    private PaymentGateway gateway;

    private PaymentStatus paymentStatus;

    private BigDecimal amount;

    private String currency;

    private String gatewayOrderId;

    private String gatewayPaymentId;

}