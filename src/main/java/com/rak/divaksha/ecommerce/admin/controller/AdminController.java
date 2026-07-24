package com.rak.divaksha.ecommerce.admin.controller;

import com.rak.divaksha.ecommerce.admin.dto.AdminDashboardResponse;
import com.rak.divaksha.ecommerce.admin.service.AdminDashboardService;
import com.rak.divaksha.ecommerce.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping("/dashboard")
    public ApiResponse<AdminDashboardResponse> dashboard() {

        return ApiResponse.<AdminDashboardResponse>builder()
                .success(true)
                .message("Dashboard fetched successfully")
                .data(adminDashboardService.getDashboard())
                .build();

    }

}