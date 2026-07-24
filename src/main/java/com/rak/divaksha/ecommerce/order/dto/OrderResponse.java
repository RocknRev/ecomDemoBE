package com.rak.divaksha.ecommerce.order.dto;

import com.rak.divaksha.ecommerce.common.enums.OrderStatus;
import com.rak.divaksha.ecommerce.common.enums.PaymentMethod;
import com.rak.divaksha.ecommerce.common.enums.PaymentStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class OrderResponse {

    private Long id;

    private String orderNumber;

    private OrderStatus orderStatus;

    private PaymentStatus paymentStatus;

    private PaymentMethod paymentMethod;

    private BigDecimal subtotal;

    private BigDecimal shippingCharge;

    private BigDecimal taxAmount;

    private BigDecimal discountAmount;

    private BigDecimal totalAmount;

    private String fullName;

    private String phone;

    private String email;

    private String addressLine1;

    private String addressLine2;

    private String city;

    private String state;

    private String postalCode;

    private String country;

    private String notes;

    private LocalDateTime createdAt;

    private List<OrderItemResponse> items;

}