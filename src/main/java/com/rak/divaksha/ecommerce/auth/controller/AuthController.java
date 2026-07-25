package com.rak.divaksha.ecommerce.auth.controller;


import com.rak.divaksha.ecommerce.auth.dto.LoginRequest;
import com.rak.divaksha.ecommerce.auth.dto.RegisterRequest;
import com.rak.divaksha.ecommerce.auth.dto.AuthResponse;
import com.rak.divaksha.ecommerce.auth.service.AuthService;
import com.rak.divaksha.ecommerce.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        return ApiResponse.<AuthResponse>builder()
                .success(true)
                .message("Registration successful")
                .data(authService.register(request))
                .build();
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {

        return ApiResponse.<AuthResponse>builder()
                .success(true)
                .message("Login successful")
                .data(authService.login(request))
                .build();
    }

}