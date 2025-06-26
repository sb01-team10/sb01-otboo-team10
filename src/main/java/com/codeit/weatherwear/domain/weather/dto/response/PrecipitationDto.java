package com.codeit.weatherwear.domain.weather.dto.response;

import com.codeit.weatherwear.domain.weather.entity.PrecipitationsType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PrecipitationDto {

  private final PrecipitationsType type;  // 강수 타입
  private final double amount;  // 강수량
  private final double probability; // 강수 확률
}
