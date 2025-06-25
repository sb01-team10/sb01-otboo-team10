package com.codeit.weatherwear.domain.clothes.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record ClothesAttributeDefUpdateRequest(
    String name,
    @NotEmpty(message = "선택값 입력은 필수입니다.") List<String> selectValues
) {
}
