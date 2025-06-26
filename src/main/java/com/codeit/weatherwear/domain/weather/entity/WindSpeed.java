package com.codeit.weatherwear.domain.weather.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WindSpeed {

  @Column(name = "wind_speed", nullable = false)
  private double speed;

  @Enumerated(EnumType.STRING)
  @Column(name = "wind_speed_as_word", nullable = false)
  private WindSpeedType speedAsWord;

  @Builder
  private WindSpeed(double speed, WindSpeedType speedAsWord) {
    this.speed = speed;
    this.speedAsWord = speedAsWord;
  }
}
