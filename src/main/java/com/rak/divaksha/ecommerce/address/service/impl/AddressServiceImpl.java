package com.rak.divaksha.ecommerce.address.service.impl;

import com.rak.divaksha.ecommerce.address.dto.CreateAddressRequest;
import com.rak.divaksha.ecommerce.address.dto.UpdateAddressRequest;
import com.rak.divaksha.ecommerce.address.dto.AddressResponse;
import com.rak.divaksha.ecommerce.address.entity.Address;
import com.rak.divaksha.ecommerce.address.repository.AddressRepository;
import com.rak.divaksha.ecommerce.address.service.AddressService;
import com.rak.divaksha.ecommerce.auth.entity.User;
import com.rak.divaksha.ecommerce.auth.repository.UserRepository;
import com.rak.divaksha.ecommerce.exception.ResourceNotFoundException;
import com.rak.divaksha.ecommerce.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    private User currentUser() {

        CustomUserDetails user =
                (CustomUserDetails) SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getPrincipal();

        return userRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    }

    private void clearDefault(User user) {

        addressRepository.findByUserIdAndIsDefaultTrue(user.getId())
                .ifPresent(address -> {
                    address.setIsDefault(false);
                    addressRepository.save(address);
                });

    }

    @Override
    public AddressResponse create(CreateAddressRequest request) {

        User user = currentUser();

        if (Boolean.TRUE.equals(request.getIsDefault())) {
            clearDefault(user);
        }

        Address address = new Address();

        address.setUser(user);
        address.setType(request.getType());
        address.setFullName(request.getFullName());
        address.setPhone(request.getPhone());
        address.setEmail(request.getEmail());
        address.setAddressLine1(request.getAddressLine1());
        address.setAddressLine2(request.getAddressLine2());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPostalCode(request.getPostalCode());
        address.setCountry(request.getCountry());
        address.setLabel(request.getLabel());
        address.setIsDefault(request.getIsDefault());

        address = addressRepository.save(address);

        return map(address);

    }

    @Override
    public AddressResponse update(Long id, UpdateAddressRequest request) {

        User user = currentUser();

        Address address = addressRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        if (request.getType() != null)
            address.setType(request.getType());

        if (request.getFullName() != null)
            address.setFullName(request.getFullName());

        if (request.getPhone() != null)
            address.setPhone(request.getPhone());

        if (request.getEmail() != null)
            address.setEmail(request.getEmail());

        if (request.getAddressLine1() != null)
            address.setAddressLine1(request.getAddressLine1());

        if (request.getAddressLine2() != null)
            address.setAddressLine2(request.getAddressLine2());

        if (request.getCity() != null)
            address.setCity(request.getCity());

        if (request.getState() != null)
            address.setState(request.getState());

        if (request.getPostalCode() != null)
            address.setPostalCode(request.getPostalCode());

        if (request.getCountry() != null)
            address.setCountry(request.getCountry());

        if (request.getLabel() != null)
            address.setLabel(request.getLabel());

        if (Boolean.TRUE.equals(request.getIsDefault())) {
            clearDefault(user);
            address.setIsDefault(true);
        }

        address = addressRepository.save(address);

        return map(address);

    }

    @Override
    @Transactional(readOnly = true)
    public AddressResponse get(Long id) {

        User user = currentUser();

        Address address = addressRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        return map(address);

    }

    @Override
    @Transactional(readOnly = true)
    public java.util.List<AddressResponse> getAll() {

        User user = currentUser();

        return addressRepository.findByUserIdOrderByIsDefaultDescCreatedAtDesc(user.getId())
                .stream()
                .map(this::map)
                .toList();

    }

    @Override
    public void delete(Long id) {

        User user = currentUser();

        Address address = addressRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        addressRepository.delete(address);

    }

    @Override
    public void setDefault(Long id) {

        User user = currentUser();

        Address address = addressRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        clearDefault(user);

        address.setIsDefault(true);

        addressRepository.save(address);

    }

    private AddressResponse map(Address address) {

        return AddressResponse.builder()
                .id(address.getId())
                .type(address.getType())
                .fullName(address.getFullName())
                .phone(address.getPhone())
                .email(address.getEmail())
                .addressLine1(address.getAddressLine1())
                .addressLine2(address.getAddressLine2())
                .city(address.getCity())
                .state(address.getState())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .label(address.getLabel())
                .isDefault(address.getIsDefault())
                .build();

    }

}