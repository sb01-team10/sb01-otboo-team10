package com.codeit.weatherwear.domain.weather.service.impl;

import com.codeit.weatherwear.domain.location.dto.LocationDto;
import com.codeit.weatherwear.domain.location.entity.Location;
import com.codeit.weatherwear.domain.location.mapper.LocationMapper;
import com.codeit.weatherwear.domain.location.repository.LocationRepository;
import com.codeit.weatherwear.domain.location.service.LocationService;
import com.codeit.weatherwear.domain.weather.dto.response.WeatherDto;
import com.codeit.weatherwear.domain.weather.entity.Weather;
import com.codeit.weatherwear.domain.weather.mapper.WeatherMapper;
import com.codeit.weatherwear.domain.weather.repository.WeatherRepository;
import com.codeit.weatherwear.domain.weather.service.WeatherFetchService;
import com.codeit.weatherwear.domain.weather.service.WeatherService;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherServiceImpl implements WeatherService {

  private final LocationService locationService;
  private final LocationRepository locationRepository;
  private final LocationMapper locationMapper;
  private final WeatherFetchService weatherFetchService;
  private final WeatherRepository weatherRepository;
  private final WeatherMapper weatherMapper;

  private static final ZoneId KST = ZoneId.of("Asia/Seoul");

  /**
   * 날씨 정보 요청 API
   *
   * @param latitude  위도
   * @param longitude 경도
   * @return WeatherDto 리스트 반환
   */
  @Transactional
  @Override
  public List<WeatherDto> getWeatherInfo(double latitude, double longitude) {
    Location location = locationService.getLocation(latitude, longitude);
    LocationDto locationDto = locationMapper.toDto(location);
    // 있다면 그냥 가져오기
    LocalDate today = LocalDate.now(KST);

    Instant todayStart = today.atStartOfDay(KST).toInstant();    // 오늘 00:00
    List<Weather> weatherList = weatherRepository.findRecentWeathers(location, todayStart);

    if (weatherList.isEmpty()) {
      // 없다면 가져오기
      weatherList = weatherFetchService.fetchAndStoreWeather(latitude, longitude);
    }

    weatherList.sort(Comparator.comparing(Weather::getForecastAt));

    return weatherList.stream().map(weather -> weatherMapper.toDto(weather, locationDto)).collect(
        Collectors.toList());
  }

  /**
   * 위치 정보 요청 API
   *
   * @param latitude  위도
   * @param longitude 경도
   * @return LocationDto 반환
   */
  @Transactional
  @Override
  public LocationDto getLocationInfo(double latitude, double longitude) {
    boolean isExist = locationRepository.existsLocationByLatitudeAndLongitude(latitude, longitude);
    if (isExist) {
      return locationMapper.toDto(locationService.getLocation(latitude, longitude));
    } else {
      Location location = locationService.findOrCreateByGeoPoint(latitude, longitude);
      return locationMapper.toDto(location);
    }
  }
}
