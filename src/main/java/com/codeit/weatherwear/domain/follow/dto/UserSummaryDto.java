package com.codeit.weatherwear.domain.follow.dto;

import com.codeit.weatherwear.domain.user.entity.User;
import java.util.UUID;

public record UserSummaryDto(
    UUID userId,
    String name,
    String profileImageUrl
) {
  public static UserSummaryDto from(User user) {
    return new UserSummaryDto(
        user.getId(),
        user.getName(),
        user.getProfileImageUrl()
    );
  }
}
