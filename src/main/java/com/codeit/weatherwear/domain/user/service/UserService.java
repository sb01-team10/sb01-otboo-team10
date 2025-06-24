package com.codeit.weatherwear.domain.user.service;

import com.codeit.weatherwear.domain.user.dto.UserCreateRequest;
import com.codeit.weatherwear.domain.user.dto.UserDto;

public interface UserService {

    public UserDto create(UserCreateRequest userCreateRequest);
}
