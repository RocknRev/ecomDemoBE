package com.rak.divaksha.ecommerce.auth.repository;

import com.rak.divaksha.ecommerce.auth.entity.EmailOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EmailOtpRepository extends JpaRepository<EmailOtp, Long> {

    Optional<EmailOtp> findTopByEmailOrderByCreatedAtDesc(String email);

    void deleteByEmail(String email);

    void deleteByExpiryTimeBefore(LocalDateTime time);
}