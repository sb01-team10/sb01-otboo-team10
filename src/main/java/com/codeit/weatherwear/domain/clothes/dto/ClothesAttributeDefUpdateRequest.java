package com.codeit.weatherwear.domain.clothes.dto;

import java.util.List;

public record ClothesAttributeDefUpdateRequest(
    String name,
    List<String> selectValues
) {
}
