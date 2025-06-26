package com.codeit.weatherwear.domain.weather.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Temperature {

  @Column(name = "temperature_current", nullable = false)
  private double current;

  @Column(name = "temperature_compared_to_day_before", nullable = false)
  private double comparedToDayBefore;

  @Column(name = "temperature_min", nullable = false)
  private double min;

  @Column(name = "temperature_max", nullable = false)
  private double max;

  @Builder
  private Temperature(double current, double comparedToDayBefore, double min, double max) {
    this.current = current;
    this.comparedToDayBefore = comparedToDayBefore;
    this.min = min;
    this.max = max;
  }
}
