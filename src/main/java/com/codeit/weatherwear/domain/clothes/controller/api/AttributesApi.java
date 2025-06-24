package com.codeit.weatherwear.domain.clothes.controller.api;

import com.codeit.weatherwear.domain.clothes.dto.ClothesAttributeDefCreateRequest;
import com.codeit.weatherwear.domain.clothes.dto.ClothesAttributeDefDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

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
}
