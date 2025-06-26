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

  public static WindSpeedType fromCode(double wsdValue) {
    if (wsdValue < 4.0) {
      return WindSpeedType.NONE;
    } else if (wsdValue < 9.0) {
      return WindSpeedType.WEAK;
    } else if (wsdValue < 14.0) {
      return WindSpeedType.MODERATE;
    } else {
      return WindSpeedType.STRONG;
    }
  }
}
