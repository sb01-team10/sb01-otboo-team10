package com.codeit.weatherwear.domain.clothes.controller;

import com.codeit.weatherwear.domain.clothes.controller.api.AttributesApi;
import com.codeit.weatherwear.domain.clothes.dto.request.AttributesSearchRequest;
import com.codeit.weatherwear.domain.clothes.dto.request.ClothesAttributeDefCreateRequest;
import com.codeit.weatherwear.domain.clothes.dto.request.ClothesAttributeDefUpdateRequest;
import com.codeit.weatherwear.domain.clothes.dto.response.ClothesAttributeDefDto;
import com.codeit.weatherwear.domain.clothes.service.AttributesService;
import com.codeit.weatherwear.global.response.PageResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
     * @param dto 속성 정보(이름, 후보값들)
     * @return 201 400
     */
    @Override
    @PostMapping
    public ResponseEntity<ClothesAttributeDefDto> create(@Valid @RequestBody ClothesAttributeDefCreateRequest dto) {
        ClothesAttributeDefDto createAttribute = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createAttribute);
    }

    /**
     * 의상 속성을 수정합니다.
     *
     * @param definitionId,request 속성 정보(이름, 후보값들)
     * @return 200 400
     */
    @Override
    @PatchMapping("/{definitionId}")
    public ResponseEntity<ClothesAttributeDefDto> update(
        @PathVariable UUID definitionId,
        @Valid @RequestBody ClothesAttributeDefUpdateRequest request) {
        ClothesAttributeDefDto updateAttribute = service.update(definitionId, request);
        return ResponseEntity.status(HttpStatus.OK).body(updateAttribute);
    }

    /**
     * 의상 속성을 삭제합니다.
     *
     * @param definitionId
     * @return 204 400
     */
    @Override
    @DeleteMapping("/{definitionId}")
    public ResponseEntity<Void> delete(@PathVariable UUID definitionId) {
        service.delete(definitionId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 의상 속성을 조회합니다.
     *
     * @param request 조회 조건
     * @return 200 400
     */
    @Override
    @GetMapping
    public ResponseEntity<PageResponse<ClothesAttributeDefDto>> searchAttributes(
        @ModelAttribute @Valid AttributesSearchRequest request) {
        PageResponse<ClothesAttributeDefDto> result = service.searchAttributes(request);
        return ResponseEntity.ok(result);
    }
}
