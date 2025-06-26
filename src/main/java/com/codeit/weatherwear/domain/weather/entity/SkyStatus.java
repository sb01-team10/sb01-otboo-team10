package com.codeit.weatherwear.domain.weather.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SkyStatus {
  CLEAR(1),
  MOSTLY_CLOUDY(3),
  CLOUDY(4);

  private final int skyStatusCode;

  public static SkyStatus fromCode(int skyCode) {
    for (SkyStatus type : values()) {
      if (type.getSkyStatusCode() == skyCode) {
        return type;
      }
    }
    return SkyStatus.CLEAR;
  }
}
