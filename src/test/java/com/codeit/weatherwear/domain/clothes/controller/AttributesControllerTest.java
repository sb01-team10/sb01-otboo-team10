package com.codeit.weatherwear.domain.clothes.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.codeit.weatherwear.domain.clothes.dto.ClothesAttributeDefCreateRequest;
import com.codeit.weatherwear.domain.clothes.dto.ClothesAttributeDefDto;
import com.codeit.weatherwear.domain.clothes.repository.AttributesRepository;
import com.codeit.weatherwear.domain.clothes.service.AttributesService;
import com.codeit.weatherwear.global.exception.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AttributesController.class)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
public class AttributesControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private AttributesService service;
    @MockitoBean
    private AttributesRepository repository;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/clothes/attribute-defs - 성공")
    void save_success() throws Exception {
        //given
        ClothesAttributeDefCreateRequest request = new ClothesAttributeDefCreateRequest(
            UUID.randomUUID(),
            "색상",
            List.of("빨강", "파랑"));
        ClothesAttributeDefDto dto = new ClothesAttributeDefDto(request.definitionId(),
            "색상",List.of("빨강", "파랑"));

        given(service.create(any(ClothesAttributeDefCreateRequest.class))).willReturn(dto);

        //when
        //then
        mockMvc.perform(
            post("/api/clothes/attribute-defs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))

                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("색상"))
                .andExpect(jsonPath("$.selectableValues[0]").value("빨강"))
                .andExpect(jsonPath("$.selectableValues[1]").value("파랑"));
    }
}
