package com.codeit.weatherwear.domain.feed.mapper;

import com.codeit.weatherwear.domain.feed.dto.request.FeedCreateRequest;
import com.codeit.weatherwear.domain.feed.dto.response.FeedDto;
import com.codeit.weatherwear.domain.feed.entity.Feed;
import com.codeit.weatherwear.domain.follow.dto.UserSummaryDto;
import com.codeit.weatherwear.domain.ootd.dto.response.OotdDto;
import com.codeit.weatherwear.domain.user.entity.User;
import com.codeit.weatherwear.domain.weather.dto.response.WeatherSummaryDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FeedMapper {

  public FeedDto toDto(Feed feed, UserSummaryDto author, WeatherSummaryDto weather,
      List<OotdDto> ootds, boolean likedByMe) {
    return FeedDto.builder()
        .id(feed.getId())
        .createdAt(feed.getCreatedAt())
        .updatedAt(feed.getUpdatedAt())
        .author(author)
        .weather(weather)
        .ootds(ootds)
        .content(feed.getContent())
        .commentCount(feed.getCommentCount())
        .likeCount(feed.getLikeCount())
        .likedByMe(likedByMe)
        .build();
  }

  public Feed toEntity(User author, FeedCreateRequest feedCreateRequest) {
    return Feed.builder()
        .author(author)
        .content(feedCreateRequest.getContent())
        .likeCount(0)
        .commentCount(0)
        .build();
  }

}
