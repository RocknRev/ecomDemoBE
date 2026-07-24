package com.rak.divaksha.ecommerce.auth.controller;

import com.rak.divaksha.ecommerce.auth.dto.ChangePasswordRequest;
import com.rak.divaksha.ecommerce.auth.dto.UpdateProfileRequest;
import com.rak.divaksha.ecommerce.auth.dto.UserProfileResponse;
import com.rak.divaksha.ecommerce.auth.service.UserService;
import com.rak.divaksha.ecommerce.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ApiResponse<UserProfileResponse> profile() {

        return ApiResponse.<UserProfileResponse>builder()
                .success(true)
                .message("Profile fetched successfully")
                .data(userService.getProfile())
                .build();
    }

    @PutMapping("/me")
    public ApiResponse<UserProfileResponse> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request) {

        return ApiResponse.<UserProfileResponse>builder()
                .success(true)
                .message("Profile updated successfully")
                .data(userService.updateProfile(request))
                .build();
    }

    @PutMapping("/change-password")
    public ApiResponse<Void> changePassword(
            @Valid @RequestBody ChangePasswordRequest request) {

        userService.changePassword(request);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("Password changed successfully")
                .build();
    }
}