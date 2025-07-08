package com.codeit.weatherwear.domain.clothes.mapper;

import com.codeit.weatherwear.domain.clothes.dto.response.ClothesAttributeDefDto;
import com.codeit.weatherwear.domain.clothes.dto.response.ClothesAttributeWithDefDto;
import com.codeit.weatherwear.domain.clothes.dto.response.RecommendClothesDto;
import com.codeit.weatherwear.domain.clothes.entity.Cloth;
import com.codeit.weatherwear.domain.clothes.entity.ClothWithAttributes;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RecommendClothesMapper {

  public RecommendClothesDto toDto(Cloth cloth, String imageUrl) {
    List<ClothesAttributeWithDefDto> clothesAttributeWithDefDtos = cloth.getClothesWithAttributes()
        .stream()
        .map(attr -> new ClothesAttributeWithDefDto(
            attr.getAttribute().getId(),
            attr.getAttribute().getName(),
            attr.getAttribute().getSelectableValues(),
            attr.getValue()
        ))
        .toList();

    return RecommendClothesDto.builder()
        .clothesId(cloth.getId())
        .ownerId(cloth.getUser().getId())
        .name(cloth.getName())
        .imageUrl(imageUrl)
        .type(cloth.getClothType())
        .attributes(clothesAttributeWithDefDtos)
        .build();

  }

  public List<ClothesAttributeDefDto> toAttributeDefDto(List<ClothWithAttributes> attrs) {
    return attrs
        .stream().map(
            attr -> new ClothesAttributeDefDto(attr.getId(), attr.getAttribute().getName(),
                attr.getAttribute().getSelectableValues())
        )
        .toList();
  }
}
