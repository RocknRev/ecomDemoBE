package com.rak.divaksha.ecommerce.auth.service;

public interface MailService {

    void sendOtpMail(String email, String otp);

}