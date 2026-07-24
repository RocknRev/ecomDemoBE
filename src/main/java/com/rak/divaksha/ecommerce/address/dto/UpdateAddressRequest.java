package com.rak.divaksha.ecommerce.address.dto;

import com.rak.divaksha.ecommerce.common.enums.AddressType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateAddressRequest {

    private AddressType type;

    private String fullName;

    private String phone;

    private String email;

    private String addressLine1;

    private String addressLine2;

    private String city;

    private String state;

    private String postalCode;

    private String country;

    private String label;

    private Boolean isDefault;

}