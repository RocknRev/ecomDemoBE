package com.rak.divaksha.ecommerce.payment.entity;

import com.rak.divaksha.ecommerce.common.entity.BaseEntity;
import com.rak.divaksha.ecommerce.common.enums.PaymentGateway;
import com.rak.divaksha.ecommerce.common.enums.PaymentStatus;
import com.rak.divaksha.ecommerce.order.entity.Order;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "t_payments")
public class Payment extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    private String paymentId;

    private String gatewayOrderId;

    private String gatewayPaymentId;

    @Column(length = 500)
    private String gatewaySignature;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentGateway gateway;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private String currency = "INR";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    private LocalDateTime transactionTime;

    @Column(columnDefinition = "TEXT")
    private String failureReason;

    @Column(columnDefinition = "TEXT")
    private String rawResponse;

}