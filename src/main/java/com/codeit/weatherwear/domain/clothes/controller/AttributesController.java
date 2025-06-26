package com.codeit.weatherwear.domain.clothes.controller;

import com.codeit.weatherwear.domain.clothes.controller.api.AttributesApi;
import com.codeit.weatherwear.domain.clothes.dto.request.AttributesSortDirection;
import com.codeit.weatherwear.domain.clothes.dto.request.ClothesAttributeDefCreateRequest;
import com.codeit.weatherwear.domain.clothes.dto.request.ClothesAttributeDefUpdateRequest;
import com.codeit.weatherwear.domain.clothes.dto.response.AttributesPageResponse;
import com.codeit.weatherwear.domain.clothes.dto.response.ClothesAttributeDefDto;
import com.codeit.weatherwear.domain.clothes.service.AttributesService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public ResponseEntity<ClothesAttributeDefDto> create(@Validated @RequestBody ClothesAttributeDefCreateRequest dto) {
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
        @Validated @RequestBody ClothesAttributeDefUpdateRequest request) {
        ClothesAttributeDefDto updateAttribute = service.update(definitionId, request);
        return ResponseEntity.status(HttpStatus.OK).body(updateAttribute);
    }

    /**
     * 의상 속성을 삭제합니다.
     *
     * @param definitionId
     * @return 200 400
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
     * @param cursor 커서 페이지네이션 커서
     * @param idAfter 보조 커서
     * @param limit 페이지 크기
     * @param sortBy 정렬 기준
     * @param sortDirection 정렬 방향
     * @param keywordLike 검색어
     * @return 200 400
     */
    @Override
    @GetMapping
    public ResponseEntity<AttributesPageResponse<ClothesAttributeDefDto>> searchAttributes(
        @RequestParam(required = false) String cursor,
        @RequestParam(required = false) UUID idAfter,
        @RequestParam int limit,
        @RequestParam String sortBy,
        @RequestParam AttributesSortDirection sortDirection,
        @RequestParam(required = false) String keywordLike
    ) {
        AttributesPageResponse<ClothesAttributeDefDto> result = service.searchAttributes(cursor, idAfter, limit, sortBy,
            sortDirection,keywordLike);
        return ResponseEntity.ok(result);
    }
}
