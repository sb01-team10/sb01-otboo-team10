package com.codeit.weatherwear.domain.clothes.service;

import com.codeit.weatherwear.domain.clothes.dto.ClothesAttributeDefCreateRequest;
import com.codeit.weatherwear.domain.clothes.dto.ClothesAttributeDefDto;

public interface AttributesService {
    ClothesAttributeDefDto create(ClothesAttributeDefCreateRequest request);
}
