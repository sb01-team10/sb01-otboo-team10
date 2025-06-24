package com.codeit.weatherwear.domain.clothes.mapper;

import com.codeit.weatherwear.domain.clothes.dto.ClothesAttributeDefDto;
import com.codeit.weatherwear.domain.clothes.entity.Attributes;
import org.mapstruct.Mapper;

@Mapper
public interface AttributesMapper {

    ClothesAttributeDefDto toDto(Attributes attributes);
}
