package com.codeit.weatherwear.domain.clothes.mapper;

import com.codeit.weatherwear.domain.clothes.dto.response.ClothesAttributeWithDefDto;
import com.codeit.weatherwear.domain.clothes.dto.response.ClothesAttributeDefDto;
import com.codeit.weatherwear.domain.clothes.dto.response.ClothesDto;
import com.codeit.weatherwear.domain.clothes.entity.Cloth;
import com.codeit.weatherwear.domain.clothes.entity.ClothWithAttributes;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClothMapper {

  public ClothesDto toDto(Cloth cloth, String imageUrl) {
    List<ClothesAttributeWithDefDto> clothesAttributeWithDefDtos = cloth.getClothesWithAttributes()
        .stream()
        .map(attr -> new ClothesAttributeWithDefDto(
            attr.getAttribute().getId(),
            attr.getAttribute().getName(),
            attr.getAttribute().getSelectableValues(),
            attr.getValue()
        ))
        .toList();

    return ClothesDto.builder()
        .id(cloth.getId())
        .ownerId(cloth.getUser().getId())
        .name(cloth.getName())
        .imageUrl(imageUrl)
        .type(cloth.getClothType())
        .attributes(clothesAttributeWithDefDtos)
        .build();

  }

  //TODO: 이미지 로직 완성되면 제거할 것!! OOTD필드에서도 사용하니 확인하기
  public ClothesDto toDto(Cloth cloth) {
    List<ClothesAttributeWithDefDto> clothesAttributeWithDefDtos = cloth.getClothesWithAttributes()
        .stream()
        .map(attr -> new ClothesAttributeWithDefDto(
            attr.getAttribute().getId(),
            attr.getAttribute().getName(),
            attr.getAttribute().getSelectableValues(),
            attr.getValue()
        ))
        .toList();

    return ClothesDto.builder()
        .id(cloth.getId())
        .ownerId(cloth.getUser().getId())
        .name(cloth.getName())
        .imageUrl(cloth.getClothesImageUrl())
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
