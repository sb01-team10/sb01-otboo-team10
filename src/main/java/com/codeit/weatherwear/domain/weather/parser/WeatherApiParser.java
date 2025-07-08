package com.codeit.weatherwear.domain.weather.parser;

import com.codeit.weatherwear.domain.weather.entity.WeatherApiData;
import com.codeit.weatherwear.domain.weather.entity.WeatherApiDataId;
import com.codeit.weatherwear.domain.weather.exception.WeatherApiParsingException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class WeatherApiParser {

  private static final Set<String> TARGET_CATEGORIES = Set.of(
      "POP", "PTY", "PCP",  // 강수
      "REH",  // 습도
      "TMP", "TMN", "TMX",  // 기온
      "SKY",  // 하늘 상태
      "WSD"   // 풍속
  );

  /**
   * 단기예보 API JSON 응답을 파싱하여 WeatherApiData 리스트로 변환한 뒤,<br>예보 날짜(fcstDate) → 예보 시간(fcstTime) 기준으로
   * 그룹핑하여 반환
   *
   * @param mapper       Jackson ObjectMapper
   * @param responseBody 단기예보 API 응답의 JSON body 문자열
   * @return 예보 날짜 → 예보 시간 → 해당 시간대 예보 데이터 리스트로 구성된 Map
   */
  public Map<String, Map<String, List<WeatherApiData>>> parse(ObjectMapper mapper,
      String responseBody) {
    try {
      JsonNode root = mapper.readTree(responseBody); // 전체 JSON
      JsonNode itemsNode = root.path("response").path("body").path("items").path("item");

      // 엔티티 저장할 리스트
      List<WeatherApiData> parsedDataList = new ArrayList<>();

      // 아이템들 반복 돌림
      for (JsonNode item : itemsNode) {
        String category = item.path("category").asText();

        // 강수량, 하늘 상태 등 필요한 카테고리만 필터링
        if (!isTargetCategory(category)) {
          continue;
        }

        // 타겟 카테고리 및 데이터일 경우, WeatherApiData 생성
        WeatherApiDataId id = WeatherApiDataId.builder()
            .baseDate(item.path("baseDate").asText())
            .baseTime(item.path("baseTime").asText())
            .category(category)
            .build();

        WeatherApiData entity = WeatherApiData.builder()
            .id(id)
            .fcstDate(item.path("fcstDate").asText())
            .fcstTime(item.path("fcstTime").asText())
            .fcstValue(item.path("fcstValue").asText())
            .nx(item.path("nx").asInt())
            .ny(item.path("ny").asInt())
            .build();

        parsedDataList.add(entity);

      }

      // 예보 날짜 및 예보 시간으로 그룹핑
      Map<String, Map<String, List<WeatherApiData>>> groupedByDateAndTime =
          parsedDataList.stream()
              .collect(Collectors.groupingBy(
                  WeatherApiData::getFcstDate,
                  Collectors.groupingBy(WeatherApiData::getFcstTime)
              ));

      return groupedByDateAndTime;

    } catch (JsonProcessingException jpe) {
      throw new WeatherApiParsingException();
    }
  }

  /**
   * 타겟 카테고리 (필요한 카테고리인지) 여부 응답
   *
   * @param category
   * @return Boolean
   */
  private boolean isTargetCategory(String category) {
    return TARGET_CATEGORIES.contains(category);
  }

}
