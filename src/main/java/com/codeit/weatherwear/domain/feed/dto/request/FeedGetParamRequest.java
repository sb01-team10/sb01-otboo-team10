package com.codeit.weatherwear.domain.feed.dto.request;

import com.codeit.weatherwear.domain.feed.dto.condition.FeedSearchCondition;
import com.codeit.weatherwear.domain.weather.entity.PrecipitationsType;
import com.codeit.weatherwear.domain.weather.entity.SkyStatus;
import com.codeit.weatherwear.global.request.SortDirection;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FeedGetParamRequest {

  private String cursor;
  private UUID idAfter;

  @NotNull(message = "limit는 필수입니다.")
  @Min(value = 1, message = "limit는 1 이상이어야 합니다.")
  private Integer limit;

  @NotBlank(message = "sortBy는 필수입니다.")
  private String sortBy;

  @NotBlank(message = "sortDirection은 필수입니다.")
  private String sortDirection;

  private String keywordLike;
  private String skyStatusEqual;
  private String precipitationTypeEqual;
  private UUID authorIdEqual;

  public FeedSearchCondition toSearchCondition() {
    return FeedSearchCondition.builder()
        .idAfter(this.getIdAfter())
        .limit(this.getLimit())
        .sortBy(this.sortBy)
        .sortDirection(SortDirection.valueOf(this.getSortDirection().toUpperCase()))
        .keywordLike(this.getKeywordLike())
        .skyStatusEqual(this.getSkyStatusEqual() != null ? SkyStatus.valueOf(
            this.getSkyStatusEqual().toUpperCase()) : null)
        .precipitationsTypeEqual(
            this.getPrecipitationTypeEqual() != null ? PrecipitationsType.valueOf(
                this.getPrecipitationTypeEqual().toUpperCase()) : null)
        .authorIdEqual(this.getAuthorIdEqual())
        .build();
  }

}
