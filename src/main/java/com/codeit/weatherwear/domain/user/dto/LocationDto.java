package com.codeit.weatherwear.domain.user.dto;

import java.util.List;

public record LocationDto(
    Double latitude,
    Double logitude,
    Integer x,
    Integer y,
    List<String> locationNames
) {

}
