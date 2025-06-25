package com.codeit.weatherwear.domain.clothes.integration;

import static org.junit.jupiter.api.Assertions.*;

import com.codeit.weatherwear.domain.clothes.dto.ClothesAttributeDefCreateRequest;
import com.codeit.weatherwear.domain.clothes.dto.ClothesAttributeDefDto;
import com.codeit.weatherwear.global.config.JpaConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(JpaConfig.class)
public class AttributesTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("속성 생성 - 성공")
    @Transactional
    void create_success() throws Exception {
        //given
        ClothesAttributeDefCreateRequest request = new ClothesAttributeDefCreateRequest(
            "색상",
            List.of("빨강", "파랑"));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String json = objectMapper.writeValueAsString(request);
        HttpEntity<String> requestEntity = new HttpEntity<>(json, headers);

        //when
        ResponseEntity<ClothesAttributeDefDto> response = restTemplate.postForEntity(
            "/api/clothes/attribute-defs", requestEntity, ClothesAttributeDefDto.class);
        //then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody().id());
        assertEquals("색상", response.getBody().name());
        assertEquals(List.of("빨강","파랑"), response.getBody().selectableValues());
    }
}
