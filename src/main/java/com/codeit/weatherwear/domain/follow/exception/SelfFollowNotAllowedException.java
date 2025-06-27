package com.codeit.weatherwear.domain.follow.exception;

import com.codeit.weatherwear.global.exception.CustomException;
import com.codeit.weatherwear.global.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class SelfFollowNotAllowedException extends CustomException {

  public SelfFollowNotAllowedException(ErrorCode errorCode, Map<String, Object> details) {
    super(errorCode, details);
  }

  public static SelfFollowNotAllowedException withId(UUID id) {
    return new SelfFollowNotAllowedException(ErrorCode.SELF_FOLLOW_NOT_ALLOWED, Map.of("id", id));
  }
}
