package com.codeit.weatherwear.domain.user.dto.request;

import com.codeit.weatherwear.domain.user.entity.Role;

public record UserRoleUpdateRequest(
    Role role
) {

}
