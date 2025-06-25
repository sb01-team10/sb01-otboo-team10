package com.codeit.weatherwear.domain.user.service;

import com.codeit.weatherwear.domain.user.dto.request.ChangePasswordRequest;
import com.codeit.weatherwear.domain.user.dto.request.ProfileUpdateRequest;
import com.codeit.weatherwear.domain.user.dto.request.UserCreateRequest;
import com.codeit.weatherwear.domain.user.dto.request.UserLockUpdateRequest;
import com.codeit.weatherwear.domain.user.dto.request.UserRoleUpdateRequest;
import com.codeit.weatherwear.domain.user.dto.response.ProfileDto;
import com.codeit.weatherwear.domain.user.dto.response.UserDto;
import java.util.UUID;

public interface UserService {

    UserDto create(UserCreateRequest userCreateRequest);

    ProfileDto findProfile(UUID userId);

    ProfileDto updateProfile(UUID userId, ProfileUpdateRequest profileUpdateRequest);

    UUID updateLock(UUID userId, UserLockUpdateRequest userLockUpdateRequest);

    void updatePassword(UUID userId, ChangePasswordRequest changePasswordRequest);

    UserDto updateRole(UUID userId, UserRoleUpdateRequest userRoleUpdateRequest);
}
