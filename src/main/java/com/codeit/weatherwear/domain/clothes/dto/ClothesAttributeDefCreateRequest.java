package com.codeit.weatherwear.domain.clothes.dto;


import java.util.List;

public record ClothesAttributeDefCreateRequest(
    String name,
    List<String> selectValues) {
}
