package com.rak.divaksha.ecommerce.payment.service;

import com.razorpay.Order;
import org.json.JSONObject;

public interface RazorpayGatewayService {

    Order createOrder(
            String receipt,
            String currency,
            long amount
    ) throws Exception;

    boolean verifySignature(
            String razorpayOrderId,
            String razorpayPaymentId,
            String razorpaySignature
    );

    JSONObject fetchPayment(
            String paymentId
    ) throws Exception;

}