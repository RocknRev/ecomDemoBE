package com.rak.divaksha.ecommerce.payment.service.impl;


import com.rak.divaksha.ecommerce.common.enums.OrderStatus;
import com.rak.divaksha.ecommerce.common.enums.PaymentGateway;
import com.rak.divaksha.ecommerce.common.enums.PaymentStatus;
import com.rak.divaksha.ecommerce.exception.BadRequestException;
import com.rak.divaksha.ecommerce.exception.ResourceNotFoundException;
import com.rak.divaksha.ecommerce.order.entity.Order;
import com.rak.divaksha.ecommerce.order.repository.OrderRepository;
import com.rak.divaksha.ecommerce.payment.dto.CreatePaymentRequest;
import com.rak.divaksha.ecommerce.payment.dto.VerifyPaymentRequest;
import com.rak.divaksha.ecommerce.payment.dto.CreatePaymentResponse;
import com.rak.divaksha.ecommerce.payment.dto.PaymentResponse;
import com.rak.divaksha.ecommerce.payment.entity.Payment;
import com.rak.divaksha.ecommerce.payment.repository.PaymentRepository;
import com.rak.divaksha.ecommerce.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Value("${payment.razorpay.key-id:}")
    private String razorpayKeyId;

    @Override
    public CreatePaymentResponse createPayment(CreatePaymentRequest request) {

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found"));

        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            throw new BadRequestException("Order already paid");
        }

        Payment payment = paymentRepository.findByOrderId(order.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        payment.setGateway(request.getGateway());

        switch (request.getGateway()) {

            case COD -> payment.setGatewayOrderId(null);

            case RAZORPAY -> {

                /*
                 * Replace this section with actual Razorpay Order API.
                 */

                payment.setGatewayOrderId(
                        "order_" + UUID.randomUUID().toString().replace("-", "")
                );

            }

            default -> throw new BadRequestException(
                    "Gateway not supported yet");

        }

        paymentRepository.save(payment);

        return CreatePaymentResponse.builder()
                .paymentId(payment.getId())
                .gatewayOrderId(payment.getGatewayOrderId())
                .gatewayKey(
                        payment.getGateway() == PaymentGateway.RAZORPAY
                                ? razorpayKeyId
                                : null
                )
                .currency(payment.getCurrency())
                .amount(payment.getAmount().toPlainString())
                .build();

    }

    @Override
    public PaymentResponse verifyPayment(VerifyPaymentRequest request) {

        Payment payment = paymentRepository.findByOrderId(request.getOrderId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Payment not found"));

        Order order = payment.getOrder();

        switch (payment.getGateway()) {

            case COD -> {

                payment.setPaymentStatus(PaymentStatus.PENDING);

                order.setPaymentStatus(PaymentStatus.PENDING);

            }

            case RAZORPAY -> {

                /*
                 * Replace this section with Razorpay Signature Verification.
                 */

                payment.setGatewayPaymentId(request.getGatewayPaymentId());

                payment.setGatewaySignature(request.getGatewaySignature());

                payment.setPaymentStatus(PaymentStatus.PAID);

                payment.setTransactionTime(LocalDateTime.now());

                order.setPaymentStatus(PaymentStatus.PAID);
                order.setOrderStatus(OrderStatus.CONFIRMED);
            }

            default -> throw new BadRequestException(
                    "Gateway not supported");

        }

        payment.setRawResponse(
                "Gateway Order Id : " + request.getGatewayOrderId()
        );

        paymentRepository.save(payment);

        orderRepository.save(order);

        return map(payment);

    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPayment(Long orderId) {

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Payment not found"));

        return map(payment);

    }

    private PaymentResponse map(Payment payment) {

        return PaymentResponse.builder()
                .paymentId(payment.getId())
                .orderId(payment.getOrder().getId())
                .gateway(payment.getGateway())
                .paymentStatus(payment.getPaymentStatus())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .gatewayOrderId(payment.getGatewayOrderId())
                .gatewayPaymentId(payment.getGatewayPaymentId())
                .build();

    }

}