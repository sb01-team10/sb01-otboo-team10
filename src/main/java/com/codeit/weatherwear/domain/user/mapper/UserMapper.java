package com.codeit.weatherwear.domain.user.mapper;

import com.codeit.weatherwear.domain.location.mapper.LocationMapper;
import com.codeit.weatherwear.domain.user.dto.response.ProfileDto;
import com.codeit.weatherwear.domain.user.dto.response.UserDto;
import com.codeit.weatherwear.domain.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = LocationMapper.class)
public interface UserMapper {

    UserDto toDto(User user);

    @Mapping(source = "id", target = "userId")
    ProfileDto toProfileDto(User user);
}
