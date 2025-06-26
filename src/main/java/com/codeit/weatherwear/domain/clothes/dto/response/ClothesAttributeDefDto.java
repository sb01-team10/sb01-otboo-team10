package com.codeit.weatherwear.domain.clothes.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.UUID;

public record ClothesAttributeDefDto(
    UUID id,
    @NotBlank(message = "속성명을 입력해주세요") String name,
    @NotEmpty(message = "선택값 입력은 필수입니다.") List<String> selectableValues
) {

}
