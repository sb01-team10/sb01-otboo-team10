package com.codeit.weatherwear.domain.weather.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WindSpeedType {
  NONE,
  WEAK,
  MODERATE,
  STRONG;

  private static final double WEAK_THRESHOLD = 4.0;
  private static final double MODERATE_THRESHOLD = 9.0;
  private static final double STRONG_THRESHOLD = 14.0;

  public static WindSpeedType fromCode(double wsdValue) {
    if (wsdValue < WEAK_THRESHOLD) {
      return WindSpeedType.NONE;
    } else if (wsdValue < MODERATE_THRESHOLD) {
      return WindSpeedType.WEAK;
    } else if (wsdValue < STRONG_THRESHOLD) {
      return WindSpeedType.MODERATE;
    } else {
      return WindSpeedType.STRONG;
    }
  }
}
