package com.rak.divaksha.ecommerce.auth.service;

public interface OtpService {

    void sendOtp(String email);

    void verifyOtp(String email, String otp);

    boolean isVerified(String email);

    void clearOtp(String email);
}