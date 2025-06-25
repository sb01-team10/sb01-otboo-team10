package com.codeit.weatherwear.domain.follow.repository;

import com.codeit.weatherwear.domain.follow.dto.FollowSummaryDto;
import java.util.UUID;

public interface FollowRepositoryCustom {
  FollowSummaryDto  getSummary(UUID userId, UUID myId);
}
