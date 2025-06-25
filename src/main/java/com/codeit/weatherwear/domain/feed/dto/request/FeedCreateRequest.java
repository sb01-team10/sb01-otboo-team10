package com.codeit.weatherwear.domain.feed.dto.request;

import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FeedCreateRequest {

  private final UUID authorId;
  private final UUID weatherId;
  private final List<UUID> clothesIds;
  private final String content;
}
