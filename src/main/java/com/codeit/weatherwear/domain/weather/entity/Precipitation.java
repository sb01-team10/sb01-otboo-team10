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
public class Precipitation {

  @Enumerated(EnumType.STRING)
  @Column(name = "precipitation_type", nullable = false)
  private PrecipitationsType type;

  @Column(name = "precipitation_amount", nullable = false)
  private double amount;

  @Column(name = "precipitation_probability", nullable = false)
  private double probability;

  @Builder
  private Precipitation(double probability, double amount, PrecipitationsType type) {
    this.probability = probability;
    this.amount = amount;
    this.type = type;
  }
}
