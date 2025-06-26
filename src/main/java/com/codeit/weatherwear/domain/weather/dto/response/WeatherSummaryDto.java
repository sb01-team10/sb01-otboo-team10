package com.codeit.weatherwear.domain.weather.dto.response;

import com.codeit.weatherwear.domain.weather.entity.SkyStatus;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class WeatherSummaryDto {

  private final UUID weatherId;
  private final SkyStatus skyStatus;
  private final PrecipitationDto precipitation;
  private final TemperatureDto temperature;
}
