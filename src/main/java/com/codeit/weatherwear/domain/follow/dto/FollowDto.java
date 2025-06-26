package com.codeit.weatherwear.domain.follow.dto;

import com.codeit.weatherwear.domain.follow.Follow;
import java.time.Instant;
import java.util.UUID;

public record FollowDto(
    UUID id,
    Instant created,
    UserSummaryDto followee,
    UserSummaryDto follower
) {

  public static FollowDto from(Follow follow) {
    return new FollowDto(
        follow.getId(),
        follow.getCreatedAt(),
        com.codeit.weatherwear.domain.follow.dto.UserSummaryDto.from(follow.getFollowee()),
        com.codeit.weatherwear.domain.follow.dto.UserSummaryDto.from(follow.getFollower())
    );
  }
}
