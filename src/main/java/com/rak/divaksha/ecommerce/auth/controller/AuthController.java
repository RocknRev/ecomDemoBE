package com.rak.divaksha.ecommerce.auth.controller;


import com.rak.divaksha.ecommerce.auth.dto.*;
import com.rak.divaksha.ecommerce.auth.service.AuthService;
import com.rak.divaksha.ecommerce.auth.service.OtpService;
import com.rak.divaksha.ecommerce.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final OtpService otpService;

    @PostMapping("/register/send-otp")
    public ApiResponse<String> sendOtp(
            @Valid @RequestBody SendOtpRequest request) {

        otpService.sendOtp(request.getEmail());

        return ApiResponse.<String>builder()
                .success(true)
                .message("OTP sent successfully")
                .data("SUCCESS")
                .build();
    }

    @PostMapping("/register/verify-otp")
    public ApiResponse<String> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request) {

        otpService.verifyOtp(
                request.getEmail(),
                request.getOtp());

        return ApiResponse.<String>builder()
                .success(true)
                .message("OTP verified successfully")
                .data("VERIFIED")
                .build();

    }

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