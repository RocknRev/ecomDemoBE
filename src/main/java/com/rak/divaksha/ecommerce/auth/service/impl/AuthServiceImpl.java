package com.rak.divaksha.ecommerce.auth.service.impl;

import com.rak.divaksha.ecommerce.auth.dto.LoginRequest;
import com.rak.divaksha.ecommerce.auth.dto.RegisterRequest;
import com.rak.divaksha.ecommerce.auth.dto.AuthResponse;
import com.rak.divaksha.ecommerce.auth.entity.Role;
import com.rak.divaksha.ecommerce.auth.entity.User;
import com.rak.divaksha.ecommerce.auth.repository.RoleRepository;
import com.rak.divaksha.ecommerce.auth.repository.UserRepository;
import com.rak.divaksha.ecommerce.auth.service.AuthService;
import com.rak.divaksha.ecommerce.auth.service.OtpService;
import com.rak.divaksha.ecommerce.common.enums.RoleName;
import com.rak.divaksha.ecommerce.exception.BadRequestException;
import com.rak.divaksha.ecommerce.security.CustomUserDetails;
import com.rak.divaksha.ecommerce.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final OtpService otpService;

    @Override
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        if (!otpService.isVerified(request.getEmail())) {
            throw new BadRequestException("Email is not verified");
        }

        Role customerRole = roleRepository.findByName(RoleName.ROLE_CUSTOMER)
                .orElseThrow(() -> new BadRequestException("Customer role not found"));

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail().toLowerCase().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.getRoles().add(customerRole);

        user = userRepository.save(user);

        String token = jwtUtil.generateToken(new CustomUserDetails(user));
        otpService.clearOtp(user.getEmail());

        return AuthResponse.builder()
                .userId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .token(token)
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        String token = jwtUtil.generateToken(new CustomUserDetails(user));

        return AuthResponse.builder()
                .userId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .token(token)
                .build();
    }

}