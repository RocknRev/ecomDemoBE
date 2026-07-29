package com.rak.divaksha.ecommerce.payment.service.impl;

import com.rak.divaksha.ecommerce.common.enums.OrderStatus;
import com.rak.divaksha.ecommerce.common.enums.PaymentGateway;
import com.rak.divaksha.ecommerce.common.enums.PaymentStatus;
import com.rak.divaksha.ecommerce.exception.BadRequestException;
import com.rak.divaksha.ecommerce.exception.ResourceNotFoundException;
import com.rak.divaksha.ecommerce.order.entity.Order;
import com.rak.divaksha.ecommerce.order.entity.OrderItem;
import com.rak.divaksha.ecommerce.order.repository.OrderRepository;
import com.rak.divaksha.ecommerce.payment.dto.CreatePaymentRequest;
import com.rak.divaksha.ecommerce.payment.dto.CreatePaymentResponse;
import com.rak.divaksha.ecommerce.payment.dto.PaymentResponse;
import com.rak.divaksha.ecommerce.payment.dto.VerifyPaymentRequest;
import com.rak.divaksha.ecommerce.payment.entity.Payment;
import com.rak.divaksha.ecommerce.payment.repository.PaymentRepository;
import com.rak.divaksha.ecommerce.payment.service.PaymentService;
import com.rak.divaksha.ecommerce.payment.service.RazorpayGatewayService;
import com.rak.divaksha.ecommerce.product.entity.Product;
import com.rak.divaksha.ecommerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final RazorpayGatewayService razorpayGatewayService;
    private final ProductRepository productRepository;

    @Value("${payment.razorpay.key-id:}")
    private String razorpayKeyId;

    @Override
    public CreatePaymentResponse createPayment(CreatePaymentRequest request) throws Exception {

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found"));

        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            throw new BadRequestException("Order already paid");
        }

        Payment payment = paymentRepository.findByOrderId(order.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Payment not found"));

        payment.setGateway(request.getGateway());

        switch (request.getGateway()) {

            case COD -> payment.setGatewayOrderId(null);

            case RAZORPAY -> {

                long amountInPaise = payment.getAmount()
                        .multiply(BigDecimal.valueOf(100))
                        .longValue();

                com.razorpay.Order razorpayOrder =
                        razorpayGatewayService.createOrder(
                                order.getOrderNumber(),
                                payment.getCurrency(),
                                amountInPaise
                        );

                payment.setGatewayOrderId(
                        razorpayOrder.get("id")
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
    @Transactional
    public PaymentResponse verifyPayment(VerifyPaymentRequest request) throws Exception {

        Payment payment = paymentRepository.findByOrderId(request.getOrderId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Payment not found"));

        Order order = payment.getOrder();

        /*
         * Prevent duplicate verification.
         */
        if (payment.getPaymentStatus() == PaymentStatus.PAID) {
            throw new BadRequestException("Payment already verified");
        }

        switch (payment.getGateway()) {

            case COD -> {

                payment.setPaymentStatus(PaymentStatus.PENDING);

                order.setPaymentStatus(PaymentStatus.PENDING);

                order.setOrderStatus(OrderStatus.PENDING);

            }

            case RAZORPAY -> {

                /*
                 * Prevent duplicate Razorpay payment IDs.
                 */
                paymentRepository.findByGatewayPaymentId(
                        request.getGatewayPaymentId()
                ).ifPresent(existing -> {

                    if (!existing.getId().equals(payment.getId())) {

                        throw new BadRequestException(
                                "Duplicate Razorpay payment detected."
                        );

                    }

                });

                /*
                 * NEVER trust gatewayOrderId received from frontend.
                 * Use the value stored in database.
                 */
                boolean verified =
                        razorpayGatewayService.verifySignature(

                                payment.getGatewayOrderId(),

                                request.getGatewayPaymentId(),

                                request.getGatewaySignature()

                        );

                if (!verified) {

                    payment.setPaymentStatus(PaymentStatus.FAILED);

                    payment.setFailureReason(
                            "Invalid Razorpay signature"
                    );

                    paymentRepository.save(payment);

                    throw new BadRequestException(
                            "Payment signature verification failed."
                    );

                }

                /*
                 * Fetch payment directly from Razorpay.
                 */
                JSONObject paymentInfo =
                        razorpayGatewayService.fetchPayment(
                                request.getGatewayPaymentId()
                        );

                /*
                 * Validate Amount.
                 */
                long gatewayAmount =
                        paymentInfo.getLong("amount");

                long expectedAmount =
                        payment.getAmount()
                                .multiply(BigDecimal.valueOf(100))
                                .longValue();

                if (gatewayAmount != expectedAmount) {

                    payment.setPaymentStatus(PaymentStatus.FAILED);

                    payment.setFailureReason(
                            "Amount mismatch"
                    );

                    paymentRepository.save(payment);

                    throw new BadRequestException(
                            "Amount mismatch detected."
                    );

                }

                /*
                 * Validate Currency.
                 */
                String gatewayCurrency =
                        paymentInfo.getString("currency");

                if (!payment.getCurrency()
                        .equalsIgnoreCase(gatewayCurrency)) {

                    payment.setPaymentStatus(
                            PaymentStatus.FAILED
                    );

                    payment.setFailureReason(
                            "Currency mismatch"
                    );

                    paymentRepository.save(payment);

                    throw new BadRequestException(
                            "Currency mismatch."
                    );

                }

                /*
                 * Validate Razorpay Status.
                 */
                String gatewayStatus =
                        paymentInfo.getString("status");

                if (!"captured".equalsIgnoreCase(gatewayStatus)) {

                    payment.setPaymentStatus(
                            PaymentStatus.FAILED
                    );

                    payment.setFailureReason(
                            "Payment not captured"
                    );

                    paymentRepository.save(payment);

                    throw new BadRequestException(
                            "Payment is not captured."
                    );

                }

                /*
                 * Update Payment.
                 */
                payment.setGatewayPaymentId(
                        request.getGatewayPaymentId()
                );

                payment.setGatewaySignature(
                        request.getGatewaySignature()
                );

                payment.setTransactionTime(
                        LocalDateTime.now()
                );

                payment.setPaymentStatus(
                        PaymentStatus.PAID
                );

                payment.setFailureReason(null);

                payment.setRawResponse(
                        paymentInfo.toString(4)
                );

                /*
                 * Update Order.
                 */
                order.setPaymentStatus(
                        PaymentStatus.PAID
                );

                order.setOrderStatus(
                        OrderStatus.CONFIRMED
                );

                /*
                 * Reduce Inventory.
                 */
                for (OrderItem item : order.getItems()) {

                    Product product = item.getProduct();

                    if (product.getStock() < item.getQuantity()) {

                        payment.setPaymentStatus(
                                PaymentStatus.FAILED
                        );

                        payment.setFailureReason(
                                "Insufficient stock for "
                                        + product.getName()
                        );

                        paymentRepository.save(payment);

                        throw new BadRequestException(
                                product.getName()
                                        + " is out of stock."
                        );

                    }

                    product.setStock(
                            product.getStock()
                                    - item.getQuantity()
                    );

                    productRepository.save(product);

                }

            }

            default -> throw new BadRequestException(
                    "Unsupported payment gateway."
            );

        }

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

    /**
     * Converts Payment Entity to DTO.
     */
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