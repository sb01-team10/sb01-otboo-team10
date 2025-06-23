package com.codeit.weatherwear.global.response;

import com.codeit.weatherwear.global.exception.CustomException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.annotation.Nullable;
import org.springframework.http.HttpStatus;

/**
 * API 응답 전달 시 사용하는 응답 객체
 *
 * @param httpStatus HTTP 상태 코드
 * @param success    성공 여부
 * @param data       응답 데이터 존재 시 데이터 전달
 * @param error      에러 응답 -> 에러 반환 시 해당 내용 반환
 * @param <T>        위 Param을 Json 형식으로 응답하도록 함.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CustomApiResponse<T>(
    HttpStatus httpStatus,
    boolean success,
    @Nullable @JsonUnwrapped T data,  // 이거 해줘야 응답을 잘 함..
    @Nullable ErrorResponse error
) {

  public static <T> CustomApiResponse<T> ok(@Nullable final T data) {
    return new CustomApiResponse<>(HttpStatus.OK, true, data, null);
  }

  public static <T> CustomApiResponse<T> created(@Nullable final T data) {
    return new CustomApiResponse<>(HttpStatus.CREATED, true, data, null);
  }

  public static <T> CustomApiResponse<T> fail(final CustomException e) {
    return new CustomApiResponse<>(e.getErrorCode().getStatus(), false, null,
        ErrorResponse.of(e.getErrorCode()));
  }
}
