package com.codeit.weatherwear.domain.weather.repository;

import com.codeit.weatherwear.domain.location.entity.Location;
import com.codeit.weatherwear.domain.weather.entity.Weather;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WeatherRepository extends JpaRepository<Weather, UUID> {

  @Query("SELECT w FROM Weather w WHERE w.location = :location AND w.forecastedAt >= :start")
  List<Weather> findRecentWeathers(@Param("location") Location location,
      @Param("start") Instant todayStart);
}
