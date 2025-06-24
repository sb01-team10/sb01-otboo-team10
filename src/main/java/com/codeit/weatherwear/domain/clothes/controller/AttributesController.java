package com.codeit.weatherwear.domain.clothes.controller;

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
public class AttributesController {
    private final AttributesService service;

    @PostMapping
    public ResponseEntity<ClothesAttributeDefDto> create(@RequestBody ClothesAttributeDefCreateRequest dto) {
        ClothesAttributeDefDto createAttribute = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createAttribute);
    }
}
