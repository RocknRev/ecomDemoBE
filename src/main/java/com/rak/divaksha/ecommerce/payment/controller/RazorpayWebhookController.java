package com.rak.divaksha.ecommerce.payment.controller;

import com.rak.divaksha.ecommerce.common.enums.OrderStatus;
import com.rak.divaksha.ecommerce.common.enums.PaymentStatus;
import com.rak.divaksha.ecommerce.payment.entity.Payment;
import com.rak.divaksha.ecommerce.payment.repository.PaymentRepository;
import com.rak.divaksha.ecommerce.order.entity.Order;
import com.rak.divaksha.ecommerce.order.repository.OrderRepository;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RazorpayWebhookController {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Value("${payment.razorpay.webhook-secret}")
    private String webhookSecret;

    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(

            @RequestBody String payload,

            @RequestHeader("X-Razorpay-Signature")
            String signature

    ) {

        try {

            /*
             * Verify Razorpay Webhook Signature
             */
            boolean valid =
                    Utils.verifyWebhookSignature(

                            payload,

                            signature,

                            webhookSecret

                    );

            if (!valid) {

                log.error("Invalid Razorpay Webhook Signature");

                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .build();

            }

            JSONObject json =
                    new JSONObject(payload);

            String event =
                    json.getString("event");

            JSONObject entity =

                    json.getJSONObject("payload")
                            .getJSONObject("payment")
                            .getJSONObject("entity");

            String paymentId =
                    entity.getString("id");

            log.info(
                    "Webhook Event : {}",
                    event
            );

            Payment payment =
                    paymentRepository
                            .findByGatewayPaymentId(
                                    paymentId
                            )
                            .orElse(null);

            if (payment == null) {

                log.warn(
                        "Payment {} not found.",
                        paymentId
                );

                return ResponseEntity.ok().build();

            }

            Order order =
                    payment.getOrder();

            /*
             * Ignore duplicate webhook events.
             */
            if (payment.getPaymentStatus() == PaymentStatus.PAID
                    && "payment.captured".equals(event)) {

                log.info(
                        "Payment already processed : {}",
                        paymentId
                );

                return ResponseEntity.ok().build();

            }

            switch (event) {

                case "payment.captured" -> {

                    payment.setPaymentStatus(
                            PaymentStatus.PAID
                    );

                    payment.setGatewayPaymentId(
                            paymentId
                    );

                    payment.setGatewaySignature(
                            signature
                    );

                    payment.setFailureReason(null);

                    payment.setRawResponse(
                            entity.toString(4)
                    );

                    if (payment.getTransactionTime() == null) {

                        payment.setTransactionTime(
                                java.time.LocalDateTime.now()
                        );

                    }

                    order.setPaymentStatus(
                            PaymentStatus.PAID
                    );

                    /*
                     * Don't overwrite completed orders.
                     */
                    if (order.getOrderStatus() != OrderStatus.DELIVERED
                            && order.getOrderStatus() != OrderStatus.SHIPPED) {

                        order.setOrderStatus(
                                OrderStatus.CONFIRMED
                        );

                    }

                    paymentRepository.save(payment);

                    orderRepository.save(order);

                    log.info(
                            "Payment {} captured successfully.",
                            paymentId
                    );

                }

                case "payment.failed" -> {

                    payment.setPaymentStatus(
                            PaymentStatus.FAILED
                    );

                    payment.setFailureReason(
                            entity.optString(
                                    "error_description",
                                    "Payment Failed"
                            )
                    );

                    payment.setRawResponse(
                            entity.toString(4)
                    );

                    order.setPaymentStatus(
                            PaymentStatus.FAILED
                    );

                    if (order.getOrderStatus()
                            == OrderStatus.PENDING) {

                        order.setOrderStatus(
                                OrderStatus.CANCELLED
                        );

                    }

                    paymentRepository.save(payment);

                    orderRepository.save(order);

                    log.warn(
                            "Payment {} failed.",
                            paymentId
                    );

                }

                default ->

                        log.info(
                                "Ignoring Razorpay Event : {}",
                                event
                        );

            }

            return ResponseEntity.ok().build();

        } catch (Exception ex) {

            log.error(
                    "Webhook processing failed.",
                    ex
            );

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();

        }

    }

}