package com.rak.divaksha.ecommerce.auth.service.impl;

import com.rak.divaksha.ecommerce.auth.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendOtpMail(String email, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(email);

        message.setSubject("FoodZone Email Verification OTP");

        message.setText("""
                Welcome to FoodZone!

                Your One-Time Password (OTP) is:

                %s

                This OTP is valid for 5 minutes.

                If you didn't request this OTP, please ignore this email.

                Regards,
                FoodZone Team
                """.formatted(otp));

        mailSender.send(message);
    }
}