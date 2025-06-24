package com.codeit.weatherwear.domain.location.mapper;

import com.codeit.weatherwear.domain.location.dto.LocationDto;
import com.codeit.weatherwear.domain.location.entity.Location;
import java.util.Arrays;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    @Mapping(target = "locationNames", expression = "java(splitLocationName(location.getName()))")
    @Mapping(target = "latitude", source = "latitude")
    @Mapping(target = "longitude", source = "longitude")
    LocationDto toDto(Location location);

    // Location의 name을 List<String>으로 표현
    // ex) "서울시 마포구 연남동" -> ["서울시", "마포구", "연남동"]
    default List<String> splitLocationName(String name) {
        if (name == null || name.isBlank()) {
            return List.of();
        }
        return Arrays.stream(name.trim().split("\\s+"))
            .filter(s -> !s.isBlank())
            .toList();
    }
}
