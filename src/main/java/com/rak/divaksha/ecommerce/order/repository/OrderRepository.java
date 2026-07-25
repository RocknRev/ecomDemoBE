package com.rak.divaksha.ecommerce.order.repository;

import com.rak.divaksha.ecommerce.common.enums.OrderStatus;
import com.rak.divaksha.ecommerce.common.enums.PaymentStatus;
import com.rak.divaksha.ecommerce.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Order> findByIdAndUserId(Long id, Long userId);

    Optional<Order> findByOrderNumber(String orderNumber);

    long countByOrderStatus(OrderStatus status);

    List<Order> findAllByOrderByCreatedAtDesc();

    List<Order> findByOrderStatusOrderByCreatedAtDesc(OrderStatus orderStatus);

    @Query("""
    SELECT COALESCE(SUM(o.totalAmount),0)
    FROM Order o
    WHERE o.paymentStatus='PAID'
    """)
    BigDecimal getTotalRevenue();

    @Query("""
    SELECT COALESCE(SUM(o.totalAmount), 0)
    FROM Order o
    WHERE o.paymentStatus = :status
    AND o.createdAt >= :start
    AND o.createdAt < :end
    """)
    BigDecimal getTodayRevenue(
            @Param("status") PaymentStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}