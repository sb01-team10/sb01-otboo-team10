package com.codeit.weatherwear.domain.location.dto;

import java.util.List;

public record LocationDto(
    Double latitude,
    Double longitude,
    Integer x,
    Integer y,
    List<String> locationNames
) {

}
