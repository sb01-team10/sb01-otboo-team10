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
        UserSummaryDto.from(follow.getFollowee()),
        UserSummaryDto.from(follow.getFollower())
    );
  }
}
