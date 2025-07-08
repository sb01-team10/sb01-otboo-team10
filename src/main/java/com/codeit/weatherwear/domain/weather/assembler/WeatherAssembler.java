package com.codeit.weatherwear.domain.weather.assembler;

import com.codeit.weatherwear.domain.location.entity.Location;
import com.codeit.weatherwear.domain.weather.entity.Humidity;
import com.codeit.weatherwear.domain.weather.entity.Precipitation;
import com.codeit.weatherwear.domain.weather.entity.PrecipitationsType;
import com.codeit.weatherwear.domain.weather.entity.SkyStatus;
import com.codeit.weatherwear.domain.weather.entity.Temperature;
import com.codeit.weatherwear.domain.weather.entity.Weather;
import com.codeit.weatherwear.domain.weather.entity.WeatherApiData;
import com.codeit.weatherwear.domain.weather.entity.WindSpeed;
import com.codeit.weatherwear.domain.weather.support.WeatherCalculationHelper;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Weather 엔티티를 조립하는 Assembler 클래스
 */
@Component
@RequiredArgsConstructor
public class WeatherAssembler {

  // 복잡한 계산 및 파싱 로직 분리
  private final WeatherCalculationHelper helper;

  /**
   * 주어진 예보 데이터와 위치 정보를 바탕으로 Weather 엔티티를 조립(assemble)하여 반환한다.
   *
   * @param fcstDate       예보 날짜 (yyyyMMdd)
   * @param baseDate       기준 날짜 (yyyyMMdd)
   * @param categoryMap    카테고리별(예: TMP, REH 등) WeatherApiData 맵
   * @param location       위치 정보 엔티티
   * @param groupedApiData 날짜별/카테고리별로 그룹화된 WeatherApiData 맵
   * @return Weather 엔티티 객체
   */
  public Weather assemble(
      String fcstDate,
      String baseDate,
      Map<String, WeatherApiData> categoryMap,
      Location location,
      Map<String, Map<String, List<WeatherApiData>>> groupedApiData
  ) {
    // categoryMap에서 예보 시간 중 가장 이른(fcstTime) 값을 추출
    String fcstTime = helper.extractMinForecastTime(categoryMap);

    // 현재 습도 값 파싱
    Double currentHumidity = helper.parseDoubleOrNull(helper.getFcstValue(categoryMap, "REH"));
    // 전일 대비 습도 변화랑 계산
    Double comparedHumidity = helper.calculateDifferenceFromPreviousDay(currentHumidity, "REH",
        fcstDate, fcstTime,
        groupedApiData);

    // todo: 기온 값 등이 없다고 해서 변화량을 0이라고 둬도 되나? null로 두는 것이 나을까? (고민)
    // 현재 기온 값 파싱
    Double currentTemperature = helper.parseDoubleOrNull(helper.getFcstValue(categoryMap, "TMP"));
    // 전일 대비 기온 변화량 계산
    Double comparedTemperature = helper.calculateDifferenceFromPreviousDay(currentTemperature,
        "TMP", fcstDate, fcstTime,
        groupedApiData);

    // 최고 기온 값 파싱 (해당 카테고리가 없을 때는 0)
    Double tmx = helper.parseMinMaxTempDoubleOrNull(categoryMap.get("TMX"));
    // 최저 기온 값 파싱 (해당 카테고리가 없을 때는 0)
    Double tmn = helper.parseMinMaxTempDoubleOrNull(categoryMap.get("TMN"));

    // Weather 엔티티 생성
    return Weather.builder()
        .location(location)
        .forecastedAt(helper.toInstant(baseDate, categoryMap.get("REH").getId().getBaseTime()))
        .forecastAt(helper.toInstant(fcstDate, "1100"))
        .skyStatus(SkyStatus.fromCode(Integer.parseInt(categoryMap.get("SKY").getFcstValue())))
        .precipitation(
            Precipitation.builder()
                .type(PrecipitationsType.fromCode(categoryMap.get("PTY").getFcstValue()))
                .amount(helper.parsePrecipitationAmount(categoryMap.get("PCP").getFcstValue()))
                .probability(
                    helper.parsePrecipitationProbability(categoryMap.get("POP").getFcstValue()))
                .build()
        )
        .humidity(
            Humidity.builder()
                .current(currentHumidity)
                .comparedToDayBefore(comparedHumidity)
                .build()
        )
        .temperature(
            Temperature.builder()
                .current(currentTemperature)
                .comparedToDayBefore(comparedTemperature)
                .min(tmn)
                .max(tmx)
                .build()
        )
        .windSpeed(
            WindSpeed.builder()
                .speed(helper.parseDoubleOrNull(categoryMap.get("WSD").getFcstValue()))
                .build()
        )
        .build();
  }

}
