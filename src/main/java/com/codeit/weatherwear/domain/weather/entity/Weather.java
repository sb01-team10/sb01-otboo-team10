package com.codeit.weatherwear.domain.weather.entity;

import com.codeit.weatherwear.domain.location.entity.Location;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "weather")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Weather {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "location_id", nullable = false)
  private Location locationId;

  @Column(name = "forecasted_at", nullable = false)
  private Instant forecastedAt;

  @Column(name = "forecast_at", nullable = false)
  private Instant forecastAt;

  @Enumerated(EnumType.STRING)
  @Column(name = "sky_status", length = 10, nullable = false)
  private SkyStatus skyStatus;

  @Embedded
  private Precipitation precipitation;

  @Embedded
  private Humidity humidity;

  @Embedded
  private Temperature temperature;

  @Embedded
  private WindSpeed windSpeed;


}
