package com.rak.divaksha.ecommerce.payment.controller;

import com.rak.divaksha.ecommerce.common.dto.ApiResponse;
import com.rak.divaksha.ecommerce.payment.dto.CreatePaymentRequest;
import com.rak.divaksha.ecommerce.payment.dto.VerifyPaymentRequest;
import com.rak.divaksha.ecommerce.payment.dto.CreatePaymentResponse;
import com.rak.divaksha.ecommerce.payment.dto.PaymentResponse;
import com.rak.divaksha.ecommerce.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create")
    public ApiResponse<CreatePaymentResponse> createPayment(
            @Valid @RequestBody CreatePaymentRequest request) {

        return ApiResponse.<CreatePaymentResponse>builder()
                .success(true)
                .message("Payment created successfully")
                .data(paymentService.createPayment(request))
                .build();

    }

    @PostMapping("/verify")
    public ApiResponse<PaymentResponse> verifyPayment(
            @Valid @RequestBody VerifyPaymentRequest request) {

        return ApiResponse.<PaymentResponse>builder()
                .success(true)
                .message("Payment verified successfully")
                .data(paymentService.verifyPayment(request))
                .build();

    }

    @GetMapping("/{orderId}")
    public ApiResponse<PaymentResponse> getPayment(
            @PathVariable Long orderId) {

        return ApiResponse.<PaymentResponse>builder()
                .success(true)
                .message("Payment fetched successfully")
                .data(paymentService.getPayment(orderId))
                .build();

    }

}