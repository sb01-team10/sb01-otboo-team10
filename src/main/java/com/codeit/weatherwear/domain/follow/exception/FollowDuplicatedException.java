package com.codeit.weatherwear.domain.follow.exception;

import com.codeit.weatherwear.global.exception.CustomException;
import com.codeit.weatherwear.global.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class FollowDuplicatedException extends CustomException {

  public FollowDuplicatedException(ErrorCode errorCode, Map<String, Object> details) {
    super(errorCode, details);
  }

  public static FollowDuplicatedException withId(UUID followee, UUID follower) {
    Map<String, Object> details = Map.of("followee", followee, "follower", follower);
    return new FollowDuplicatedException(ErrorCode.FOLLOW_DUPLICATED, details);
  }
}
