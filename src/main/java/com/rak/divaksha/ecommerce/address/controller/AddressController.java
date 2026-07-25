package com.rak.divaksha.ecommerce.address.controller;

import com.rak.divaksha.ecommerce.address.dto.CreateAddressRequest;
import com.rak.divaksha.ecommerce.address.dto.UpdateAddressRequest;
import com.rak.divaksha.ecommerce.address.dto.AddressResponse;
import com.rak.divaksha.ecommerce.address.service.AddressService;
import com.rak.divaksha.ecommerce.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    public ApiResponse<List<AddressResponse>> getAll() {

        return ApiResponse.<List<AddressResponse>>builder()
                .success(true)
                .message("Addresses fetched successfully")
                .data(addressService.getAll())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<AddressResponse> get(@PathVariable Long id) {

        return ApiResponse.<AddressResponse>builder()
                .success(true)
                .message("Address fetched successfully")
                .data(addressService.get(id))
                .build();
    }

    @PostMapping
    public ApiResponse<AddressResponse> create(
            @Valid @RequestBody CreateAddressRequest request) {

        return ApiResponse.<AddressResponse>builder()
                .success(true)
                .message("Address created successfully")
                .data(addressService.create(request))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<AddressResponse> update(
            @PathVariable Long id,
            @RequestBody UpdateAddressRequest request) {

        return ApiResponse.<AddressResponse>builder()
                .success(true)
                .message("Address updated successfully")
                .data(addressService.update(id, request))
                .build();
    }

    @PatchMapping("/{id}/default")
    public ApiResponse<Void> setDefault(@PathVariable Long id) {

        addressService.setDefault(id);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("Default address updated successfully")
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {

        addressService.delete(id);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("Address deleted successfully")
                .build();
    }

}