package com.codeit.weatherwear.domain.user.service;

import com.codeit.weatherwear.domain.user.dto.ProfileUpdateRequest;
import com.codeit.weatherwear.domain.user.dto.UserCreateRequest;
import com.codeit.weatherwear.domain.user.dto.UserDto;
import java.util.UUID;

public interface UserService {

    UserDto create(UserCreateRequest userCreateRequest);

    UserDto updateProfile(UUID userId, ProfileUpdateRequest profileUpdateRequest);
}
