package com.rak.divaksha.ecommerce.auth.service;

import com.rak.divaksha.ecommerce.auth.dto.ChangePasswordRequest;
import com.rak.divaksha.ecommerce.auth.dto.UpdateProfileRequest;
import com.rak.divaksha.ecommerce.auth.dto.UserProfileResponse;

public interface UserService {

    UserProfileResponse getProfile();

    UserProfileResponse updateProfile(UpdateProfileRequest request);

    void changePassword(ChangePasswordRequest request);

}