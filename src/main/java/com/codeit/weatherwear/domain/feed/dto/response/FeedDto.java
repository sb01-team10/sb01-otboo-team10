package com.codeit.weatherwear.domain.feed.dto.response;

import com.codeit.weatherwear.domain.ootd.dto.response.OotdDto;
import com.codeit.weatherwear.domain.user.dto.response.AuthorDto;
import com.codeit.weatherwear.domain.weather.dto.response.WeatherSummaryDto;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FeedDto {

  private final UUID id;
  private final Instant createdAt;
  private final Instant updatedAt;
  private final AuthorDto author;
  private final WeatherSummaryDto weather;
  private final List<OotdDto> ootds;
  private final String content;
  private final int likeCount;
  private final int commentCount;
  private final boolean likedByMe;
}
