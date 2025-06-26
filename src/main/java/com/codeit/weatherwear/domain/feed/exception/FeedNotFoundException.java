package com.codeit.weatherwear.domain.feed.exception;

import com.codeit.weatherwear.global.exception.CustomException;
import com.codeit.weatherwear.global.exception.ErrorCode;

public class FeedNotFoundException extends CustomException {

  public FeedNotFoundException() {
    super(ErrorCode.FEED_NOT_FOUND);
  }
}
