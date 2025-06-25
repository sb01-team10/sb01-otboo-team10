package com.codeit.weatherwear.domain.clothes.service;

import com.codeit.weatherwear.domain.clothes.dto.ClothesAttributeDefCreateRequest;
import com.codeit.weatherwear.domain.clothes.dto.ClothesAttributeDefDto;
import com.codeit.weatherwear.domain.clothes.dto.ClothesAttributeDefUpdateRequest;
import java.util.UUID;

public interface AttributesService {
    ClothesAttributeDefDto create(ClothesAttributeDefCreateRequest request);
    ClothesAttributeDefDto update(UUID id,ClothesAttributeDefUpdateRequest request);

}
