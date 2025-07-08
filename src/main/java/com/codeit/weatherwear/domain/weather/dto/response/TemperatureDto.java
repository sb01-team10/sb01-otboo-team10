package com.codeit.weatherwear.domain.weather.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TemperatureDto {

  private final Double current;
  private final Double comparedToDayBefore;
  private final Double min;
  private final Double max;
}
