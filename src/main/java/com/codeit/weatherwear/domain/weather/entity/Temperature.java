package com.codeit.weatherwear.domain.weather.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Temperature {

  @Comment("현재 온도(섭씨)")
  @Column(name = "temperature_current", nullable = false)
  private double current;

  @Comment("전일 대비 온도(섭씨)")
  @Column(name = "temperature_compared_to_day_before", nullable = false)
  private double comparedToDayBefore;

  @Comment("일 최저 기온(섭씨)")
  @Column(name = "temperature_min", nullable = false)
  private double min;

  @Comment("일 최고 기온(섭씨)")
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
