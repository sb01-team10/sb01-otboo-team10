package com.codeit.weatherwear.domain.weather.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PrecipitationsType {
  NONE(0),
  RAIN(1),
  RAIN_SNOW(2),
  SNOW(3),
  SHOWER(4);

  private final int precipitationTypeCode;

  public static PrecipitationsType fromCode(String ptyCode) {
    try {
      int code = Integer.parseInt(ptyCode);
      for (PrecipitationsType type : values()) {
        if (type.getPrecipitationTypeCode() == code) {
          return type;
        }
      }
    } catch (NumberFormatException e) {
      // -, null, 0값은 ‘강수없음’
      return PrecipitationsType.NONE;
    }
    return PrecipitationsType.NONE;
  }
}
