package com.codeit.weatherwear.domain.feed.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FeedUpdateRequest {

  private final String content;
}
