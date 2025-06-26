package com.codeit.weatherwear.global.response;

import com.codeit.weatherwear.global.exception.ErrorCode;
import java.util.Map;
import lombok.Builder;

/**
 * 에러 응답 시 사용하는 응답 record.
 *
 * @param exceptionName
 * @param message   메시지 (어떤 에러가 발생했는지)
 * @param details   해당 에러에 대한 세부 사항
 */
@Builder
public record ErrorResponse(
    String exceptionName,
    String message,
    Map<String,Object> details) {

  public static ErrorResponse of(ErrorCode errorCode,
      Exception ex,
      Map<String,Object> details) {
    return ErrorResponse.builder()
        .exceptionName(ex.getClass().getSimpleName())
        .message(errorCode.getMessage())
        .details(details)
        .build();
  }
}
