package com.codeit.weatherwear.domain.user.dto;

import com.codeit.weatherwear.domain.user.entity.Gender;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record ProfileUpdateRequest(
    @Size(min = 1, max = 20, message = "이름은 1-20자 사이여야 합니다.")
    String name,
    Gender gender,
    LocalDate birthDate,
    LocationDto location,
    Integer temperatureSensitivity
) {

}
