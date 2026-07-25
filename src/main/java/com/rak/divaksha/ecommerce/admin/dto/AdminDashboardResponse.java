package com.rak.divaksha.ecommerce.admin.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class AdminDashboardResponse {

    private Long totalCustomers;

    private Long totalProducts;

    private Long totalOrders;

    private Long pendingOrders;

    private Long confirmedOrders;

    private Long shippedOrders;

    private Long deliveredOrders;

    private Long cancelledOrders;

    private BigDecimal totalRevenue;

    private BigDecimal todayRevenue;

}