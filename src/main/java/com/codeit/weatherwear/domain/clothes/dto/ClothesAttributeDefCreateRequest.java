package com.codeit.weatherwear.domain.clothes.dto;


import java.util.List;
import java.util.UUID;

public record ClothesAttributeDefCreateRequest(
    UUID definitionId,
    String definitionName,
    List<String> selectValues) {
}
