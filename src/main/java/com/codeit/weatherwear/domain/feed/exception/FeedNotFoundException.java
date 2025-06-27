package com.codeit.weatherwear.domain.feed.exception;

import com.codeit.weatherwear.global.exception.CustomException;
import com.codeit.weatherwear.global.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class FeedNotFoundException extends CustomException {

  public FeedNotFoundException(UUID feedId) {
    super(ErrorCode.FEED_NOT_FOUND, Map.of("feedId", feedId));
  }
}
