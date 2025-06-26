package com.codeit.weatherwear.domain.ootd.dto.response;

import com.codeit.weatherwear.domain.clothes.dto.response.ClothesAttributeDefDto;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OotdDto {

  private final UUID clothesId;
  private final String name;
  private final String imageUrl;
  private final String type;
  private final List<ClothesAttributeDefDto> attributes;
}
