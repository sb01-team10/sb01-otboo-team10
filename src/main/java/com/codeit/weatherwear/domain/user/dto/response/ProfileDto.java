package com.codeit.weatherwear.domain.user.dto.response;

import com.codeit.weatherwear.domain.location.dto.LocationDto;
import com.codeit.weatherwear.domain.user.entity.Gender;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProfileDto {

    private final UUID userId;
    private final String name;
    private final Gender gender;
    private final LocalDate birthDate;
    private final LocationDto location;
    private final Integer temperatureSensitivity;
    private final String profileImageUrl;
}
