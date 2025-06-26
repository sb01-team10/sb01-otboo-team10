package com.codeit.weatherwear.domain.weather.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
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
public class Humidity {

  @Comment("습도 (%)")
  @Min(value = 0, message = "현재 습도는 음수가 될 수 없습니다.")
  @Max(value = 100, message = "현재 습도는 100%를 초과할 수 없습니다.")
  @Column(name = "humidity_current", nullable = false)
  private double current;

  @Comment("전일 대비 습도 (%)")
  @Min(value = -100, message = "전일 대비 습도는 -100%보다 더 낮은 값이 될 수 없습니다.")
  @Max(value = 100, message = "전일 대비 습도는 100%보다 더 높은 값이 될 수 없습니다.")
  @Column(name = "humidity_compared_to_day_before", nullable = false)
  private double comparedToDayBefore;

  @Builder
  private Humidity(double comparedToDayBefore, double current) {
    this.comparedToDayBefore = comparedToDayBefore;
    this.current = current;
  }
}
