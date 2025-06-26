package com.codeit.weatherwear.domain.weather.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WindSpeed {

  @Comment("풍속 단위: m/s")
  @Min(value = 0, message = "풍속은 음수가 될 수 없습니다.")
  @Column(name = "wind_speed", nullable = false)
  private double speed;

  @Enumerated(EnumType.STRING)
  @Column(name = "wind_speed_as_word", nullable = false)
  private WindSpeedType speedAsWord;

  @Builder
  private WindSpeed(double speed) {
    this.speed = speed;
    this.speedAsWord = WindSpeedType.fromCode(this.speed);
  }
}
