package com.codeit.weatherwear.domain.follow.dto;

import com.codeit.weatherwear.domain.follow.Follow;
import java.util.UUID;

public record FollowDto(
    UUID id,
    UserSummaryDto followee,
    UserSummaryDto follower
) {

  public static FollowDto from(Follow follow) {
    return new FollowDto(
        follow.getId(),
        UserSummaryDto.from(follow.getFollowee()),
        UserSummaryDto.from(follow.getFollower())
    );
  }
}
