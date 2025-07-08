package com.codeit.weatherwear.domain.weather.api;

import com.codeit.weatherwear.domain.weather.exception.WeatherApiRequestException;
import com.codeit.weatherwear.domain.weather.exception.WeatherApiResponseException;
import com.codeit.weatherwear.global.properties.WeatherApiProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 기상청 단기 예보 API와 통신하여 데이터를 요청하는 역할
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WeatherApiClient {

  private final WeatherApiProperties apiProperties;

  /**
   * 단기 예보 API에 데이터를 요청하여 응답을 받아와 리턴한다.
   *
   * @param mapper   JSON 변환에 사용할 ObjectMapper
   * @param baseDate 예보 기준 날짜 (yyyyMMdd)
   * @param baseTime 예보 기준 시간 (HHmm)
   * @param nx       예보 지점 X좌표
   * @param ny       예보 지점 Y좌표
   * @return API 응답 데이터(ResponseBody)
   */
  public String fetchWeatherData(
      ObjectMapper mapper, String baseDate, String baseTime, int nx, int ny) {
    // 기본 파라미터 세팅
    String REQUEST_PARAM_DATA_TYPE = "JSON";
    int REQUEST_PARAM_NUM_OF_ROWS = 1500;

    // 요청 URL 세팅
    String requestUrl = String.format(
        "%s?serviceKey=%s&numOfRows=%d&dataType=%s&base_date=%s&base_time=%s&nx=%d&ny=%d",
        apiProperties.getApiUrl(), apiProperties.getApiServiceKey(),
        REQUEST_PARAM_NUM_OF_ROWS, REQUEST_PARAM_DATA_TYPE, baseDate, baseTime, nx, ny
    );

    // HttpClient 세팅 및 요청 세팅
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(requestUrl))
        .GET()
        .build();

    try {
      HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
      String resultCode = extractResultCode(mapper, response.body());
      // 응답 코드가 00이 아니면 비정상적인 응답 (오류)
      if (!"00".equals(resultCode)) {
        log.warn("Weather Api Response Invalid");
        throw new WeatherApiResponseException(resultCode);
      }
      // 응답 Body 전달
      return response.body();
    } catch (IOException | InterruptedException e) {
      log.error("Weather Api Request Invalid - cause: {}\nmessage: {}", e.getCause(),
          e.getMessage());
      throw new WeatherApiRequestException();
    }
  }

  private String extractResultCode(ObjectMapper mapper, String responseBody)
      throws IOException {
    // Header의 resultCode 필드 속 값 String 형태로 추출
    JsonNode root = mapper.readTree(responseBody);
    return root.path("response").path("header").path("resultCode").asText();
  }

}
