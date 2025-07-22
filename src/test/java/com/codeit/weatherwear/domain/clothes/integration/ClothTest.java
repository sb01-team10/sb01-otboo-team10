package com.codeit.weatherwear.domain.clothes.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.codeit.weatherwear.domain.clothes.dto.response.ClothesDto;
import com.codeit.weatherwear.global.config.ContainerInitializer;
import java.net.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration(initializers = ContainerInitializer.class)
//@Testcontainers
public class ClothTest {

 /* @Container
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine")
      .withDatabaseName("weatherwear")
      .withUsername("user")
      .withPassword("password");

  @DynamicPropertySource
  static void overrideProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
  }*/

  @Autowired
  private TestRestTemplate restTemplate;

  @BeforeEach
  void setUp() {
    restTemplate = restTemplate.withBasicAuth("user", "password");
  }

  /*@Test
  @DisplayName("무신사 URL에서 의상 정보 추출 - 성공")
  void extractClothesFromMusinsaUrl_success() {
    // given
    String rawUrl = "https://www.musinsa.com/products/5117573";
    URI uri = UriComponentsBuilder
        .fromPath("/api/clothes/extractions")
        .queryParam("url", rawUrl)
        .build()
        .encode()
        .toUri();

    // when
    ResponseEntity<ClothesDto> response = restTemplate.getForEntity(
        uri, ClothesDto.class
    );

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    ClothesDto dto = response.getBody();
    assertThat(dto).isNotNull();
    assertThat(dto.getName()).isNotEmpty();
    assertThat(dto.getImageUrl()).contains("http");
  }*/
}
