package com.codeit.weatherwear.domain.weather.dto.response;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class WeatherSummaryDto {

  private final UUID weatherId;
  private final String skyStatus;
  private final PrecipitationDto precipitation;
  private final TemperatureDto temperature;
}
