package com.codeit.weatherwear.domain.feed.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FeedUpdateRequest {

  @NotBlank
  private final String content;
}
