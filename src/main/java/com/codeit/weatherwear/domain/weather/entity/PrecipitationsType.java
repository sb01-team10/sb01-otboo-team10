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
    if (ptyCode == null || ptyCode.trim().isEmpty() || ptyCode.equals("-")) {
      return PrecipitationsType.NONE;
    }

    int code;
    try {
      code = Integer.parseInt(ptyCode);
      for (PrecipitationsType type : values()) {
        if (type.getPrecipitationTypeCode() == code) {
          return type;
        }
      }
    } catch (IllegalArgumentException e) {
      // 생각치 못한 예외 상황 처리를 위함
      return PrecipitationsType.NONE;
    } catch (Exception e) {
      return PrecipitationsType.NONE;
    }

    return PrecipitationsType.NONE;
  }
}
