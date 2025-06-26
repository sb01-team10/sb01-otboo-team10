package com.codeit.weatherwear.domain.weather.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Precipitation {

  @Enumerated(EnumType.STRING)
  @Comment("강수 형태")
  @Column(name = "precipitation_type", nullable = false)
  private PrecipitationsType type;

  @Comment("시간당 강수량 - 범주 (1 mm)")
  @Column(name = "precipitation_amount", nullable = false)
  private double amount;

  @Comment("강수 확률 (%)")
  @Column(name = "precipitation_probability", nullable = false)
  private double probability;

  @Builder
  private Precipitation(double probability, double amount, PrecipitationsType type) {
    this.probability = probability;
    this.amount = amount;
    this.type = type;
  }
}
