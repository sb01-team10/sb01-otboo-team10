package com.codeit.weatherwear.domain.clothes.mapper;

import com.codeit.weatherwear.domain.clothes.dto.response.ClothesAttributeDefDto;
import com.codeit.weatherwear.domain.clothes.entity.Attributes;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AttributesMapper {
    ClothesAttributeDefDto toDto(Attributes attributes);
}
