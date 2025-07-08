package com.codeit.weatherwear.domain.recommendation.dto;

import com.codeit.weatherwear.domain.clothes.dto.response.RecommendClothesDto;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecommendationDto{
  UUID weatherId;
  UUID userId;
  List<RecommendClothesDto> clothes;
}
