package com.rak.divaksha.ecommerce.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class AuthResponse {

    private Long userId;

    private String firstName;

    private String lastName;

    private String email;

    private String token;

}