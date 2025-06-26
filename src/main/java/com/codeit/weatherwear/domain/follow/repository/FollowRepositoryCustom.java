package com.codeit.weatherwear.domain.follow.repository;

import com.codeit.weatherwear.domain.follow.dto.FollowDto;
import com.codeit.weatherwear.domain.follow.dto.FollowSummaryDto;
import java.util.List;
import java.util.UUID;

public interface FollowRepositoryCustom {
  FollowSummaryDto getSummary(UUID userId, UUID myId);

  List<FollowDto> getFollowings(UUID followerId, String cursor, UUID idAfter, int limit, String nameLike);
  List<FollowDto> getFollowers(UUID followeeId, String cursor, UUID idAfter, int limit, String nameLike);
}
