package com.rak.divaksha.ecommerce.payment.service;

import com.rak.divaksha.ecommerce.payment.dto.CreatePaymentRequest;
import com.rak.divaksha.ecommerce.payment.dto.CreatePaymentResponse;
import com.rak.divaksha.ecommerce.payment.dto.PaymentResponse;
import com.rak.divaksha.ecommerce.payment.dto.VerifyPaymentRequest;

public interface PaymentService {

    CreatePaymentResponse createPayment(CreatePaymentRequest request) throws Exception;

    PaymentResponse verifyPayment(VerifyPaymentRequest request) throws Exception;

    PaymentResponse getPayment(Long orderId);

}
