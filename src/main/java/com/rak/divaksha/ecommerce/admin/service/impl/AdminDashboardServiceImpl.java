package com.rak.divaksha.ecommerce.admin.service.impl;

import com.rak.divaksha.ecommerce.admin.dto.AdminDashboardResponse;
import com.rak.divaksha.ecommerce.admin.service.AdminDashboardService;
import com.rak.divaksha.ecommerce.auth.repository.UserRepository;
import com.rak.divaksha.ecommerce.common.enums.OrderStatus;
import com.rak.divaksha.ecommerce.common.enums.PaymentStatus;
import com.rak.divaksha.ecommerce.order.repository.OrderRepository;
import com.rak.divaksha.ecommerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Override
    public AdminDashboardResponse getDashboard() {

        BigDecimal totalRevenue = orderRepository.getTotalRevenue();

        BigDecimal todayRevenue = orderRepository.getTodayRevenue(
                PaymentStatus.PAID,
                LocalDate.now().atStartOfDay(),
                LocalDate.now().plusDays(1).atStartOfDay());

        return AdminDashboardResponse.builder()
                .totalCustomers(userRepository.count())
                .totalProducts(productRepository.count())
                .totalOrders(orderRepository.count())
                .pendingOrders(orderRepository.countByOrderStatus(OrderStatus.PENDING))
                .confirmedOrders(orderRepository.countByOrderStatus(OrderStatus.CONFIRMED))
                .shippedOrders(orderRepository.countByOrderStatus(OrderStatus.SHIPPED))
                .deliveredOrders(orderRepository.countByOrderStatus(OrderStatus.DELIVERED))
                .cancelledOrders(orderRepository.countByOrderStatus(OrderStatus.CANCELLED))
                .totalRevenue(totalRevenue == null ? BigDecimal.ZERO : totalRevenue)
                .todayRevenue(todayRevenue == null ? BigDecimal.ZERO : todayRevenue)
                .build();

    }

}