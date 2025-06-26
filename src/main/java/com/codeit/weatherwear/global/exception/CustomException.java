package com.codeit.weatherwear.global.exception;

import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 커스텀 예외 클래스.
 */
@Getter
public class CustomException extends RuntimeException {

  private final ErrorCode errorCode;
  private final Map<String, Object> details;

  public CustomException(ErrorCode errorCode, Map<String, Object> details) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
    this.details = details;
  }

  @Override
  public String getMessage() {
    return errorCode.getMessage();
  }

  public String getCustomException() {
    return errorCode.getMessage() + "in Custom Exception";
  }
}
