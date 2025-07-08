package com.codeit.weatherwear.domain.weather.dto.response;

import com.codeit.weatherwear.domain.weather.entity.WindSpeedType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WindSpeedDto {

  private final Double speed;
  private final WindSpeedType asWord;
}
