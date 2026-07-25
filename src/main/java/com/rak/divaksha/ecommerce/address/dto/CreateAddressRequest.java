package com.rak.divaksha.ecommerce.address.dto;

import com.rak.divaksha.ecommerce.common.enums.AddressType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAddressRequest {

    @NotNull
    private AddressType type;

    @NotBlank
    private String fullName;

    @NotBlank
    private String phone;

    private String email;

    @NotBlank
    private String addressLine1;

    private String addressLine2;

    @NotBlank
    private String city;

    @NotBlank
    private String state;

    @NotBlank
    private String postalCode;

    @NotBlank
    private String country;

    private String label;

    private Boolean isDefault = false;

}