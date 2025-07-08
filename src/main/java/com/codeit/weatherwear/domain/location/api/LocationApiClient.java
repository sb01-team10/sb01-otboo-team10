package com.codeit.weatherwear.domain.location.api;

import com.codeit.weatherwear.domain.location.exception.KakaoGeoApiRequestException;
import com.codeit.weatherwear.domain.location.exception.KakaoGeoApiResponseException;
import com.codeit.weatherwear.domain.location.parser.LocationApiParser;
import com.codeit.weatherwear.global.properties.LocationApiProperties;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * 외부 API (카카오 지도 API - 주소 정보) 요청
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LocationApiClient {

  // 입력 좌표값
  private static final String INPUT_COORD = "WGS84";

  private final LocationApiProperties apiProperties;
  private final LocationApiParser locationApiParser;

  public List<String> getRegionNames(double latitude, double longitude) {
    // api 요청 URL 등록
    String requestUrl = String.format(
        "%s?x=%f&y=%f&input_coord=%s", apiProperties.getApiUrl(), longitude, latitude, INPUT_COORD
    );

    // HTTP 클라이언트 생성
    HttpClient client = HttpClient.newHttpClient();
    // HTTP 요청 생성
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(requestUrl))
        .header("Authorization", "KakaoAK " + apiProperties.getApiKey())
        .GET()
        .build();

    try {
      // 응답 값 받아오기 - 응답값 body bytes를 String으로 변환
      HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
      if (response.statusCode() != HttpStatus.OK.value()) {
        throw new KakaoGeoApiResponseException(response.statusCode());
      }

      // 응답 body의 주소 정보를 파싱하여 ["서울시", "종로구", "창경궁로"]와 같이 반환
      return locationApiParser.parse(response.body());
    } catch (IOException | InterruptedException e) {
      log.error("주소 정보 요청 중 오류 - cause: {}\nmessage: {}", e.getCause(), e.getMessage());
      throw new KakaoGeoApiRequestException();
    }
  }


}
