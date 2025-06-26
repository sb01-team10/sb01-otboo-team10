package com.codeit.weatherwear.domain.clothes.controller.api;

import com.codeit.weatherwear.domain.clothes.dto.request.AttributesSortDirection;
import com.codeit.weatherwear.domain.clothes.dto.request.ClothesAttributeDefCreateRequest;
import com.codeit.weatherwear.domain.clothes.dto.response.ClothesAttributeDefDto;
import com.codeit.weatherwear.domain.clothes.dto.request.ClothesAttributeDefUpdateRequest;
import com.codeit.weatherwear.global.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "의상 속성 정의", description = "의상 속성 정의 관련 API")
@RequestMapping("/api/clothes/attribute-defs")
public interface AttributesApi {

    @Operation(summary = "의상 속성 정의 등록", description = "의상 속성 정의 등록 API")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "의상 속성 정의 등록 성공",
            content = @Content(schema = @Schema(implementation = ClothesAttributeDefDto.class))),
        @ApiResponse(
            responseCode = "400",
            description = "의상 속성 정의 등록 실패",
            content = @Content(schema = @Schema(implementation = ClothesAttributeDefDto.class))
        )
    })
    @PostMapping
    ResponseEntity<ClothesAttributeDefDto> create(@RequestBody ClothesAttributeDefCreateRequest request);


    @Operation(summary = "의상 속성 정의 목록 조회", description = "의상 속성 정의 목록 조회 API")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "의상 속성 정의 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = ClothesAttributeDefDto.class))),
        @ApiResponse(
            responseCode = "400",
            description = "의상 속성 정의 목록 조회 실패",
            content = @Content(schema = @Schema(implementation = ClothesAttributeDefDto.class))
        )
    })
    @GetMapping
    ResponseEntity<PageResponse<List<ClothesAttributeDefDto>>> searchAttributes(
        @Parameter(name = "cursor", description = "커서 페이지네이션 커서")
            @RequestParam(value = "cursor", required = false)
            String cursor,
        @Parameter(name = "idAfter", description = "보조 커서")
            @RequestParam(value = "idAfter", required = false)
            UUID idAfter,
        @Parameter(name = "limit", description = "페이지 크기", example = "50")
            @RequestParam(value = "limit", defaultValue = "50",required = true)
            int limit,
        @Parameter(name = "sortBy", description = "정렬 기준", example = "sortBy")
            @RequestParam(value = "sortBy", defaultValue = "sortBy",required = true)
            String sortBy,
        @Parameter(name = "sortDirection", description = "정렬 방향", example = "ASCENDING")
            @RequestParam(value = "sortDirection",required = false)
            AttributesSortDirection sortDirection,
        @Parameter(name = "keywordLike", description = "검색어", example = "색깔")
            @RequestParam(value = "keywordLike",required = false)
            String keywordLike);



    @Operation(summary = "의상 속성 수정", description = "의상 속성 정의 수정 API")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "의상 속성 정의 수정 성공",
            content = @Content(schema = @Schema(implementation = ClothesAttributeDefDto.class))),
        @ApiResponse(
            responseCode = "400",
            description = "의상 속성 정의 수정 실패",
            content = @Content(schema = @Schema(implementation = ClothesAttributeDefDto.class))
        )
    })
    @PatchMapping("/{definitionId}")
    ResponseEntity<ClothesAttributeDefDto> update(
        @Parameter(
            name="definitionId",
            required = true
        )
        @PathVariable(value = "definitionId") UUID definitionId,
        @RequestBody ClothesAttributeDefUpdateRequest request);



    @Operation(summary = "의상 속성 삭제", description = "의상 속성 정의 삭제 API")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "의상 속성 정의 삭제 성공",
            content = @Content(schema = @Schema(implementation = ClothesAttributeDefDto.class))),
        @ApiResponse(
            responseCode = "400",
            description = "의상 속성 정의 삭제 실패",
            content = @Content(schema = @Schema(implementation = ClothesAttributeDefDto.class))
        )
    })
    @DeleteMapping("/{definitionId}")
    ResponseEntity<Void> delete(
        @Parameter(
            name="definitionId",
            required = true
        )
        @PathVariable(value = "definitionId") UUID definitionId);
}
