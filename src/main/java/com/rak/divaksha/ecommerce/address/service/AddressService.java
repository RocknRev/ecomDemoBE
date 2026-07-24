package com.rak.divaksha.ecommerce.address.service;

import com.rak.divaksha.ecommerce.address.dto.AddressResponse;
import com.rak.divaksha.ecommerce.address.dto.CreateAddressRequest;
import com.rak.divaksha.ecommerce.address.dto.UpdateAddressRequest;

import java.util.List;

public interface AddressService {

    AddressResponse create(CreateAddressRequest request);

    AddressResponse update(Long id, UpdateAddressRequest request);

    AddressResponse get(Long id);

    List<AddressResponse> getAll();

    void delete(Long id);

    void setDefault(Long id);

}