package com.codeit.weatherwear.domain.clothes.dto.request;

import com.codeit.weatherwear.domain.clothes.entity.ClothType;
import java.util.List;

public record ClothesUpdateRequest(
    String name,
    ClothType type,
    List<ClothesAttributeDto> attributes
) {

}
