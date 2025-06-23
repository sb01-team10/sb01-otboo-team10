package com.codeit.weatherwear.global.response;

import com.codeit.weatherwear.global.exception.ErrorCode;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;
import lombok.Builder;
import org.springframework.http.HttpStatus;

/**
 * 에러 응답 시 사용하는 응답 record.
 *
 * @param timestamp 에러 발생 시간
 * @param status    상태
 * @param message   메시지 (어떤 에러가 발생했는지)
 * @param details   해당 에러에 대한 세부 사항
 */
@Builder
public record ErrorResponse(
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC") Instant timestamp,
    HttpStatus status, String message, String details) {

  public static ErrorResponse of(ErrorCode errorCode) {
    return ErrorResponse.builder()
        .timestamp(Instant.now())
        .status(errorCode.getStatus())
        .message(errorCode.getMessage())
        .details(errorCode.getDetail())
        .build();
  }
}
