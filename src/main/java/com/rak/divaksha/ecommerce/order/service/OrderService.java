package com.rak.divaksha.ecommerce.order.service;


import com.rak.divaksha.ecommerce.common.enums.OrderStatus;
import com.rak.divaksha.ecommerce.order.dto.OrderResponse;
import com.rak.divaksha.ecommerce.order.dto.PlaceOrderRequest;
import com.rak.divaksha.ecommerce.order.dto.UpdateOrderStatusRequest;

import java.util.List;

public interface OrderService {

    OrderResponse placeOrder(PlaceOrderRequest request);

    OrderResponse getOrder(Long orderId);

    List<OrderResponse> getMyOrders();

    void cancelOrder(Long orderId);

    List<OrderResponse> getAllOrders();

    List<OrderResponse> getOrdersByStatus(OrderStatus status);

    OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request);

}