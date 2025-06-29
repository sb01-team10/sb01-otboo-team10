package com.codeit.weatherwear.domain.user.dto.request;

import com.codeit.weatherwear.domain.location.dto.LocationDto;
import com.codeit.weatherwear.domain.user.entity.Gender;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record ProfileUpdateRequest(
    @Size(min = 1, max = 20, message = "이름은 1-20자 사이여야 합니다.")
    String name,
    Gender gender,
    LocalDate birthDate,
    LocationDto location,
    @Min(value = 0, message = "0 이상이어야 합니다.")
    @Max(value = 5, message = "5 이하여야 합니다.")
    Integer temperatureSensitivity
) {

}
