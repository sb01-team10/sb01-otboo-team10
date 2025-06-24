package com.codeit.weatherwear.clothes.service;

import com.codeit.weatherwear.clothes.dto.ClothesAttributeDefCreateRequest;
import com.codeit.weatherwear.clothes.dto.ClothesAttributeDefDto;

public interface AttributesService {
    ClothesAttributeDefDto create(ClothesAttributeDefCreateRequest request);
}
