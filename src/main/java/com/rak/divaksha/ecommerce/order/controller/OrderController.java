package com.rak.divaksha.ecommerce.order.controller;

import com.rak.divaksha.ecommerce.common.dto.ApiResponse;
import com.rak.divaksha.ecommerce.order.dto.OrderResponse;
import com.rak.divaksha.ecommerce.order.dto.PlaceOrderRequest;
import com.rak.divaksha.ecommerce.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ApiResponse<OrderResponse> placeOrder(
            @Valid @RequestBody PlaceOrderRequest request) {

        return ApiResponse.<OrderResponse>builder()
                .success(true)
                .message("Order placed successfully")
                .data(orderService.placeOrder(request))
                .build();

    }

    @GetMapping
    public ApiResponse<List<OrderResponse>> getMyOrders() {

        return ApiResponse.<List<OrderResponse>>builder()
                .success(true)
                .message("Orders fetched successfully")
                .data(orderService.getMyOrders())
                .build();

    }

    @GetMapping("/{orderId}")
    public ApiResponse<OrderResponse> getOrder(
            @PathVariable Long orderId) {

        return ApiResponse.<OrderResponse>builder()
                .success(true)
                .message("Order fetched successfully")
                .data(orderService.getOrder(orderId))
                .build();

    }

    @PatchMapping("/{orderId}/cancel")
    public ApiResponse<Void> cancelOrder(
            @PathVariable Long orderId) {

        orderService.cancelOrder(orderId);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("Order cancelled successfully")
                .build();

    }

}
