package com.rak.divaksha.ecommerce.order.controller;


import com.rak.divaksha.ecommerce.common.dto.ApiResponse;
import com.rak.divaksha.ecommerce.common.enums.OrderStatus;
import com.rak.divaksha.ecommerce.order.dto.OrderResponse;
import com.rak.divaksha.ecommerce.order.dto.UpdateOrderStatusRequest;
import com.rak.divaksha.ecommerce.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping
    public ApiResponse<List<OrderResponse>> getAllOrders() {

        return ApiResponse.<List<OrderResponse>>builder()
                .success(true)
                .message("Orders fetched successfully")
                .data(orderService.getAllOrders())
                .build();

    }

    @GetMapping("/status/{status}")
    public ApiResponse<List<OrderResponse>> getOrdersByStatus(
            @PathVariable OrderStatus status) {

        return ApiResponse.<List<OrderResponse>>builder()
                .success(true)
                .message("Orders fetched successfully")
                .data(orderService.getOrdersByStatus(status))
                .build();

    }

    @PatchMapping("/{orderId}/status")
    public ApiResponse<OrderResponse> updateStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request) {

        return ApiResponse.<OrderResponse>builder()
                .success(true)
                .message("Order status updated successfully")
                .data(orderService.updateOrderStatus(orderId, request))
                .build();

    }

}