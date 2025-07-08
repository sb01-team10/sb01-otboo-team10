package com.codeit.weatherwear.domain.weather.service.impl;

import com.codeit.weatherwear.domain.location.entity.Location;
import com.codeit.weatherwear.domain.location.service.LocationService;
import com.codeit.weatherwear.domain.weather.api.WeatherApiClient;
import com.codeit.weatherwear.domain.weather.entity.Weather;
import com.codeit.weatherwear.domain.weather.entity.WeatherApiData;
import com.codeit.weatherwear.domain.weather.parser.WeatherApiParser;
import com.codeit.weatherwear.domain.weather.repository.WeatherRepository;
import com.codeit.weatherwear.domain.weather.service.WeatherConvertService;
import com.codeit.weatherwear.domain.weather.service.WeatherFetchService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 기상청 단기 예보 API 요청부터 응답 파싱 후 Weather 엔티티 변환까지<br>전반적인 프로세스를 정의하는 서비스 클래스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherFetchServiceImpl implements WeatherFetchService {

  // todo: KST 너무 여러 클래스에서 많이 쓰임
  private final ZoneId KST = ZoneId.of("Asia/Seoul");
  // todo: 이것도 String 말고 다른 방식으로 하는게 좋지 않을까 하는 생각이 잠시 든다 -> Enum으로 수정하고 안에 메서드로 String으로 바꾸는 걸 넣는 것은?
  private final List<String> ALLOWED_BASE_TIME = List.of(
      "0200", "0500", "0800", "1100", "1400", "1700", "2000", "2300"
  );

  private final LocationService locationService;
  private final WeatherConvertService weatherConvertService;
  private final WeatherRepository weatherRepository;

  private final WeatherApiClient weatherApiClient;
  private final WeatherApiParser weatherApiParser;

  /**
   * 기상청 단기 예보 API 요청 및 데이터 파싱을 거쳐 나온<br>날씨 데이터를 저장하고 반환
   *
   * @param latitude  위도
   * @param longitude 경도
   * @return 날씨 엔티티 리스트
   */
  @Transactional
  @Override
  public List<Weather> fetchAndStoreWeather(double latitude, double longitude) {
    Instant now = Instant.now();
    String baseDate = getBaseDate(now);
    String baseTime = getBaseTime(now);

    ObjectMapper mapper = new ObjectMapper();

    // 위치 엔티티 조회, 존재하지 않을 시 생성
    Location location = locationService.findOrCreateByGeoPoint(latitude, longitude);

    // 단기 예보 API 요청 후 응답 Body 값 반환
    String responseBody = weatherApiClient.fetchWeatherData(mapper, baseDate, baseTime,
        location.getX(), location.getY());

    // 응답 Body 값 파싱하여 Map[예보 날짜,Map[예보 타입(POP 등), 예보 RAW 데이터]] 으로 반환
    Map<String, Map<String, List<WeatherApiData>>> parsedWeatherApi = weatherApiParser.parse(mapper,
        responseBody);

    // 파싱한 데이터를 Weather 엔티티에 맞게 변환 후 저장
    List<Weather> weatherList = weatherConvertService.convert(parsedWeatherApi, location);
    weatherRepository.saveAll(weatherList);

    // Weather 리스트 반환
    return weatherList;
  }

  /**
   * Base Date 설정
   *
   * @param base Instant 객체
   * @return yyyyMMdd 형식의 기준 날짜 문자열
   */
  private String getBaseDate(Instant base) {
    return LocalDateTime.ofInstant(base, KST)
        .format(DateTimeFormatter.ofPattern("yyyyMMdd"));
  }

  /**
   * Base Time 설정
   *
   * @param base Instant 객체
   * @return HHmm 형식의 기준 시간 문자열
   */
  private String getBaseTime(Instant base) {
    final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HHmm");

    LocalTime localTime = LocalDateTime.ofInstant(base, KST).toLocalTime();

    // 현재 시각 이전(baseTime ≤ 현재시간) 중 가장 가까운 baseTime 을 찾아 "HHmm" 형식으로 반환
    // 단, 없을 경우 기본값 "2300" 반환
    return ALLOWED_BASE_TIME.stream()
        .map(t -> LocalTime.parse(t, TIME_FORMATTER))
        // 현재보다 이전/같은 시간만 필터링
        .filter(t -> !t.isAfter(localTime))
        // 가장 가까운 BaseTime 선택
        .min(Comparator.comparingInt(
            t -> Math.abs((int) Duration.between(localTime, t).toMinutes())))
        .map(t -> t.format(DateTimeFormatter.ofPattern("HHmm")))
        // 아무것도 없으면 기본 값 제공
        .orElse("2300");
  }

}
