package com.codeit.weatherwear.domain.clothes.service;

import com.codeit.weatherwear.domain.clothes.dto.request.AttributesSortDirection;
import com.codeit.weatherwear.domain.clothes.dto.request.ClothesAttributeDefCreateRequest;
import com.codeit.weatherwear.domain.clothes.dto.response.AttributesPageResponse;
import com.codeit.weatherwear.domain.clothes.dto.response.ClothesAttributeDefDto;
import com.codeit.weatherwear.domain.clothes.dto.request.ClothesAttributeDefUpdateRequest;
import java.util.UUID;

public interface AttributesService {
    ClothesAttributeDefDto create(ClothesAttributeDefCreateRequest request);

    ClothesAttributeDefDto update(UUID id, ClothesAttributeDefUpdateRequest request);

    void delete(UUID id);

    AttributesPageResponse<ClothesAttributeDefDto> searchAttributes(String cursor, UUID idAfter, int limit,
        String sortBy, AttributesSortDirection sortDirection, String keywordLike);

}
