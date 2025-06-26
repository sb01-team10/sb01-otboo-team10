package com.codeit.weatherwear.domain.feed.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FeedCreateRequest {

  @NotNull
  private final UUID authorId;

  @NotNull
  private final UUID weatherId;

  @NotEmpty
  private final List<UUID> clothesIds;

  @NotBlank
  @Size(max = 1000)
  private final String content;
}
