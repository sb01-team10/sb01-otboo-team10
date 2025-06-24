package com.codeit.weatherwear.clothes.mapper;

import com.codeit.weatherwear.clothes.dto.ClothesAttributeDefDto;
import com.codeit.weatherwear.clothes.entity.Attributes;
import org.mapstruct.Mapper;

@Mapper
public interface AttributesMapper {

    ClothesAttributeDefDto toDto(Attributes attributes);
}
