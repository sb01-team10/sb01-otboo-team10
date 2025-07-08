package com.codeit.weatherwear.domain.weather.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HumidityDto {

  private final Double current;
  private final Double comparedToDayBefore;

}
