package com.codeit.weatherwear.domain.weather;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class WeatherOpenApiTest {

  @Value("${weather.api-url}")
  private String apiUrl;

  @Value("${weather.api-service-key}")
  private String serviceKey;

  private String dataType, baseDate, baseTime;

  private int numOfRows, nx, ny;

  @BeforeEach
  void setUp() {
    // 요청할 파라미터 수치 세팅 (테스트용)
    numOfRows = 266;
    dataType = "JSON";
    baseDate = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);  // YYYYMMDD
    baseTime = "1100";
    nx = 55;
    ny = 127;
  }

  @Test
  @DisplayName("URL 및 Service key가 잘 주입되는지 확인")
  void weatherPropertiesInjectionTest() {
    // when
    assertThat(apiUrl)
        .isNotNull()
        .isNotEmpty();

    assertThat(serviceKey)
        .isNotNull()
        .isNotEmpty();
  }

  @Test
  @DisplayName("날씨 API 연결에 성공하여 데이터를 성공적으로 받아온다.")
  void weatherApiConnectionIsSuccess() throws IOException, InterruptedException {
    // given
    String requestUrl = String.format(
        "%s?serviceKey=%s&numOfRows=%d&dataType=%s&base_date=%s&base_time=%s&nx=%d&ny=%d",
        apiUrl, serviceKey, numOfRows, dataType, baseDate, baseTime, nx, ny
    );

    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(requestUrl))
        .GET()
        .build();

    HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

    assertThat(response.statusCode()).isEqualTo(200);
    assertThat(response.body()).isNotEmpty();
    assertThat(response.body())
        .as("응답에 resultCode 필드가 포함되어야 함")
        .contains("\"resultCode\"");

  }

}
