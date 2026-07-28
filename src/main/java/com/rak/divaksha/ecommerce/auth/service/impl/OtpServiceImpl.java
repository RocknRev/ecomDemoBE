package com.rak.divaksha.ecommerce.auth.service.impl;


import com.rak.divaksha.ecommerce.auth.entity.EmailOtp;
import com.rak.divaksha.ecommerce.auth.repository.EmailOtpRepository;
import com.rak.divaksha.ecommerce.auth.service.MailService;
import com.rak.divaksha.ecommerce.auth.service.OtpService;
import com.rak.divaksha.ecommerce.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private static final int MAX_ATTEMPTS = 5;
    private static final int OTP_EXPIRY_MINUTES = 5;

    private final EmailOtpRepository otpRepository;
    private final MailService mailService;

    @Override
    public void sendOtp(String email) {

        otpRepository.deleteByEmail(email);

        String otp = String.format("%06d", new Random().nextInt(1_000_000));

        EmailOtp entity = EmailOtp.builder()
                .email(email.toLowerCase().trim())
                .otp(otp)
                .expiryTime(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES))
                .verified(false)
                .attempts(0)
                .build();

        otpRepository.save(entity);

        mailService.sendOtpMail(email, otp);
        System.out.println("Email: " + email+ "|" + "OTP: " + otp);
    }

    @Override
    public void verifyOtp(String email, String otp) {

        EmailOtp entity = otpRepository
                .findTopByEmailOrderByCreatedAtDesc(email.toLowerCase().trim())
                .orElseThrow(() ->
                        new BadRequestException("OTP not found"));

        if (entity.getVerified()) {
            throw new BadRequestException("OTP already verified");
        }

        if (entity.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("OTP expired");
        }

        if (entity.getAttempts() >= MAX_ATTEMPTS) {
            throw new BadRequestException("Maximum attempts exceeded");
        }

        if (!entity.getOtp().equals(otp)) {

            entity.setAttempts(entity.getAttempts() + 1);

            otpRepository.save(entity);

            throw new BadRequestException("Invalid OTP");
        }

        entity.setVerified(true);
        entity.setVerifiedAt(LocalDateTime.now());

        otpRepository.save(entity);
    }

    @Override
    public boolean isVerified(String email) {

        return otpRepository
                .findTopByEmailOrderByCreatedAtDesc(email.toLowerCase().trim())
                .map(EmailOtp::getVerified)
                .orElse(false);
    }

    @Override
    public void clearOtp(String email) {

        otpRepository.deleteByEmail(email.toLowerCase().trim());

    }
}