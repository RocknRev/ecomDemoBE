package com.rak.divaksha.ecommerce.payment.service.impl;

import com.rak.divaksha.ecommerce.payment.service.RazorpayGatewayService;
import com.razorpay.Order;
import com.razorpay.Payment;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RazorpayGatewayServiceImpl
        implements RazorpayGatewayService {

    private final RazorpayClient razorpayClient;

    @Value("${payment.razorpay.key-secret}")
    private String keySecret;

    @Override
    public Order createOrder(
            String receipt,
            String currency,
            long amount
    ) throws Exception {

        JSONObject request = new JSONObject();

        request.put("amount", amount);

        request.put("currency", currency);

        request.put("receipt", receipt);

        request.put("payment_capture", 1);

        return razorpayClient.orders.create(request);

    }

    @Override
    public boolean verifySignature(
            String razorpayOrderId,
            String razorpayPaymentId,
            String razorpaySignature
    ) {

        try {

            JSONObject attributes = new JSONObject();

            attributes.put(
                    "razorpay_order_id",
                    razorpayOrderId
            );

            attributes.put(
                    "razorpay_payment_id",
                    razorpayPaymentId
            );

            attributes.put(
                    "razorpay_signature",
                    razorpaySignature
            );

            return Utils.verifyPaymentSignature(
                    attributes,
                    keySecret
            );

        } catch (Exception ex) {

            return false;

        }

    }

    @Override
    public JSONObject fetchPayment(
            String paymentId
    ) throws Exception {

        Payment payment =
                razorpayClient.payments.fetch(paymentId);

        return payment.toJson();

    }

}