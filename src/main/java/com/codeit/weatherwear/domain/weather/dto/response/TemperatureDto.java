package com.codeit.weatherwear.domain.weather.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TemperatureDto {

  private final double current;
  private final double comparedToDayBefore;
  private final double min;
  private final double max;
}
