package com.rak.divaksha.ecommerce.auth.service;


import com.rak.divaksha.ecommerce.auth.dto.AuthResponse;
import com.rak.divaksha.ecommerce.auth.dto.LoginRequest;
import com.rak.divaksha.ecommerce.auth.dto.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

}