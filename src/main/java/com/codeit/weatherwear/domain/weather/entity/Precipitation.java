package com.codeit.weatherwear.domain.weather.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
  @Min(value = 0, message = "강수량은 음수가 될 수 없습니다.")
  @Column(name = "precipitation_amount", nullable = false)
  private double amount;

  @Comment("강수 확률 (%)")
  @Min(value = 0, message = "강수 확률은 음수가 될 수 없습니다.")
  @Max(value = 100, message = "강수 확률은 100%를 초과할 수 없습니다.")
  @Column(name = "precipitation_probability", nullable = false)
  private double probability;

  @Builder
  private Precipitation(double probability, double amount, PrecipitationsType type) {
    this.probability = probability;
    this.amount = amount;
    this.type = type;
  }

}
