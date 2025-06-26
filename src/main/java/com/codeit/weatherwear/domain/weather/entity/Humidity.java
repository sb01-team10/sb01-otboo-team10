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
public class Humidity {

  @Column(name = "humidity_current", nullable = false)
  private double current;

  @Column(name = "humidity_compared_to_day_before", nullable = false)
  private double comparedToDayBefore;

  @Builder
  private Humidity(double comparedToDayBefore, double current) {
    this.comparedToDayBefore = comparedToDayBefore;
    this.current = current;
  }
}
