package com.codeit.weatherwear.domain.location.dto;

import java.util.List;

public record LocationDto(
    double latitude,
    double longitude,
    int x,
    int y,
    List<String> locationNames
) {

}
