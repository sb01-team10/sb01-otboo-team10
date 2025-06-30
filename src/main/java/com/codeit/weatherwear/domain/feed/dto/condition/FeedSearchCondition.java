package com.codeit.weatherwear.domain.feed.dto.condition;

import com.codeit.weatherwear.domain.weather.entity.PrecipitationsType;
import com.codeit.weatherwear.domain.weather.entity.SkyStatus;
import com.codeit.weatherwear.global.request.SortDirection;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FeedSearchCondition {
  private UUID idAfter;
  private int limit;
  private String sortBy;
  private SortDirection sortDirection;

  private String keywordLike;
  private SkyStatus skyStatusEqual;
  private PrecipitationsType precipitationsTypeEqual;
  private UUID authorIdEqual;

  public void setLimit(int limit) {
    this.limit = limit;
  }
}
