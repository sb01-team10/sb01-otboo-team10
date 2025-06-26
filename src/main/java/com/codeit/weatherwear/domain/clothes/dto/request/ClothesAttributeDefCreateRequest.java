package com.codeit.weatherwear.domain.clothes.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record ClothesAttributeDefCreateRequest(
    @NotBlank(message = "속성명을 입력해주세요") String name,
    @NotEmpty(message = "선택값 입력은 필수입니다.") List<String> selectValues) {
}
