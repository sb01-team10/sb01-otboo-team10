package com.codeit.weatherwear.domain.weather.support;

import com.codeit.weatherwear.domain.weather.entity.WeatherApiData;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 엔티티 생성 시 필요한 복잡한 계산 및 파싱 로직을 분리한 헬퍼 클래스
 * <p>
 * - 날짜 및 시간 파싱
 * <p>
 * - 카테고리 기반 값 추출
 * <p>
 * - 값 변환 및 예외 처리
 * <p>
 * - 전일 대비 값 계산
 */
@Slf4j
@Component
public class WeatherCalculationHelper {

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
  private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HHmm");
  private static final ZoneId KST = ZoneId.of("Asia/Seoul");

  private static final String DEFAULT_TIME = "1100";
  private static final String NO_PRECIPITATION = "강수없음";


  /**
   * 주어진 날짜와 시간 문자열을 KST 기준 {@link Instant} 로 변환합니다.
   *
   * @param date 날짜 (형식: yyyyMMdd)
   * @param time 시간 (형식: HHmm)
   * @return 변환된 Instant 객체 (KST 기준)
   */
  public Instant toInstant(String date, String time) {
    return LocalDateTime.of(
        LocalDate.parse(date, DATE_FORMATTER),
        LocalTime.parse(time, TIME_FORMATTER)
    ).atZone(KST).toInstant();
  }

  /**
   * 주어진 key에 해당하는 예보값(fcstValue)을 반환합니다.
   *
   * @param map 카테고리별 WeatherApiData 맵
   * @param key 추출할 카테고리 코드 (예: TMP, REH 등)
   * @return 예보값 문자열, 존재하지 않으면 null
   */

  public String getFcstValue(Map<String, WeatherApiData> map, String key) {
    return Optional.ofNullable(map.get(key))
        .map(WeatherApiData::getFcstValue)
        .orElse(null);
  }

  /**
   * 주어진 key에 해당하는 baseTime(예보 기준 시간)을 반환합니다.
   *
   * @param map 카테고리별 WeatherApiData 맵
   * @param key 추출할 카테고리 코드
   * @return baseTime 문자열, 존재하지 않으면 기본값 "1100"
   */

  public String getBaseTime(Map<String, WeatherApiData> map, String key) {
    return Optional.ofNullable(map.get(key))
        .map(data -> data.getId().getBaseTime())
        .orElse("1100");
  }

  /**
   * 문자열을 Double로 파싱합니다.<br>null 또는 잘못된 숫자 형식이면 null을 반환합니다.
   *
   * @param value 문자열 값
   * @return Double 객체 또는 null
   */
  public Double parseDoubleOrNull(String value) {
    try {
      return value != null ? Double.parseDouble(value) : null;
    } catch (NumberFormatException e) {
      log.warn("Failed Double Parsing - {}", value);
      return null;
    }
  }

  /**
   * 강수량 문자열을 실수로 변환합니다. "강수없음" 또는 null은 0.0으로 처리합니다.
   *
   * @param value 강수량 문자열
   * @return 실수형 강수량 (mm)
   */
  public Double parsePrecipitationAmount(String value) {
    if (value == null || value.equals(NO_PRECIPITATION)) {
      return null;
    }
    try {
      return Double.parseDouble(value);
    } catch (NumberFormatException e) {
      log.warn("Failed Precipitation Parsing - {}", value);
      return null;
    }
  }

  public Double parsePrecipitationProbability(String value) {
    if (value == null) {
      return null;
    }
    try {
      return Double.parseDouble(value) * 0.01;
    } catch (NumberFormatException e) {
      log.warn("Failed Precipitation Probability Parsing - {}", value);
      return null;
    }
  }

  /**
   * 현재 값과 전날 같은 시간대 예보값의 차이를 계산합니다.
   *
   * @param current        현재 값
   * @param category       카테고리 코드 (예: "REH", "TMP")
   * @param fcstDate       예보 날짜 (yyyyMMdd)
   * @param fcstTime       예보 시간 (HHmm)
   * @param groupedApiData 날짜 및 시간별 그룹화된 예보 데이터
   * @return 전일 대비 변화량 (예보값이 없으면 현재값과 동일하다고 가정 → 차이 0)
   */
  public Double calculateDifferenceFromPreviousDay(
      Double current, String category, String fcstDate, String fcstTime,
      Map<String, Map<String, List<WeatherApiData>>> groupedApiData
  ) {
    if (current == null) {
      return null;
    }

    // 전날 날짜 계산
    LocalDate previousDate = LocalDate.parse(fcstDate, DATE_FORMATTER).minusDays(1);
    String previousDateStr = previousDate.format(DATE_FORMATTER);

    // 전날 같은 시간의 예보 리스트 가져오기
    List<WeatherApiData> previousList = Optional.ofNullable(groupedApiData.get(previousDateStr))
        .map(map -> map.get(fcstTime))
        .orElse(Collections.emptyList());

    // 해당 카테고리에 해당하는 전날 예보값을 찾아 변화량 계산
    return previousList.stream()
        .filter(data -> category.equals(data.getId().getCategory()))
        .mapToDouble(data -> Double.parseDouble(data.getFcstValue()))
        .findFirst()
        .orElse(current) - current;
  }

  /**
   * 주어진 카테고리 Map에서 가장 이른 예보 시간(fcstTime)을 추출합니다.
   *
   * @param categoryMap 카테고리별 WeatherApiData 맵
   * @return 가장 빠른 fcstTime 값, 없으면 기본값 "1100"
   */
  public String extractMinForecastTime(Map<String, WeatherApiData> categoryMap) {
    return categoryMap.values().stream()
        .map(WeatherApiData::getFcstTime)
        .min(String::compareTo)
        .orElse(DEFAULT_TIME);
  }

  /**
   * 최고/최소 기온 값을 파싱합니다.<br>만약 해당 카테고리에 해당하는 데이터가 없을 때에는 0을 반환합니다.
   *
   * @param data 카테고리별 WeatherApiData
   * @return Double로 파싱된 데이터 값
   */
  public Double parseMinMaxTempDoubleOrNull(WeatherApiData data) {
    return data != null ? Double.parseDouble(data.getFcstValue()) : null;
  }

}
