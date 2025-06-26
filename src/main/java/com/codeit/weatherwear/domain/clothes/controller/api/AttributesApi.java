package com.codeit.weatherwear.domain.clothes.controller.api;

import com.codeit.weatherwear.domain.clothes.dto.request.ClothesAttributeDefCreateRequest;
import com.codeit.weatherwear.domain.clothes.dto.response.ClothesAttributeDefDto;
import com.codeit.weatherwear.domain.clothes.dto.request.ClothesAttributeDefUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
