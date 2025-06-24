package com.codeit.weatherwear.domain.user.mapper;

import com.codeit.weatherwear.domain.user.dto.UserDto;
import com.codeit.weatherwear.domain.user.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(User user);
}
