package com.codeit.weatherwear.domain.clothes.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AttributesSearchRequest(
    @Schema(description = "커서 페이지네이션 커서")
    String cursor,
    @Schema(description ="보조 커서")
    UUID idAfter,
    @Schema(description = "페이지 크기", example = "50")
    @NotNull
    @Min(1)
    @Max(100)
    int limit,
    @Schema(description = "정렬 기준", example = "sortBy")
    @NotBlank
    String sortBy,
    @Schema(description = "정렬 방향", example = "ASCENDING")
    @NotNull
    AttributesSortDirection sortDirection,
    @Schema(description = "검색어", example = "색깔")
    String keywordLike
) {
}
