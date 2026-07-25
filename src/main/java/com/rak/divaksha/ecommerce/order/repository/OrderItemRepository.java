package com.rak.divaksha.ecommerce.order.repository;

import com.rak.divaksha.ecommerce.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}