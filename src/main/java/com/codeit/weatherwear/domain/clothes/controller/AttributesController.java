package com.codeit.weatherwear.domain.clothes.controller;

import com.codeit.weatherwear.domain.clothes.controller.api.AttributesApi;
import com.codeit.weatherwear.domain.clothes.dto.ClothesAttributeDefCreateRequest;
import com.codeit.weatherwear.domain.clothes.dto.ClothesAttributeDefDto;
import com.codeit.weatherwear.domain.clothes.service.AttributesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/clothes/attribute-defs")
public class AttributesController implements AttributesApi {
    private final AttributesService service;

    /**
     * 새로운 의상 속성을 등록합니다.
     *
     * @param dto 속성 정보(id,이름, 후보값들)
     * @return 201 400
     */
    @Override
    @PostMapping
    public ResponseEntity<ClothesAttributeDefDto> create(@RequestBody ClothesAttributeDefCreateRequest dto) {
        ClothesAttributeDefDto createAttribute = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createAttribute);
    }
}
