package com.codeit.weatherwear.clothes.dto;


import java.util.List;
import java.util.UUID;

public record ClothesAttributeDefCreateRequest(
    UUID definitionId,
    String definitionName,
    List<String> selectValues) {
}
