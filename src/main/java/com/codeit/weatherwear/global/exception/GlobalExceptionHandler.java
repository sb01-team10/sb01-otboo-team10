package com.codeit.weatherwear.global.exception;

import com.codeit.weatherwear.global.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * 전역 예외 핸들러 - 전역 예외 처리를 위함.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * 존재하지 않는 요청에 대한 예외.
   *
   * @param e
   * @return 500 INTERNAL SERVER ERROR 응답
   */
  @ExceptionHandler(value = {NoHandlerFoundException.class,
      HttpRequestMethodNotSupportedException.class})
  public ResponseEntity<?> handleNoPageFoundException(Exception e) {
    log.error("GlobalExceptionHandler catch NoHandlerFoundException : {}", e.getMessage());
    return ResponseEntity
        .status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
        .body(ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR, e,e.getClass().getSimpleName()));
  }

  /**
   * 커스텀 예외에 대한 처리.
   *
   * @param e
   * @return 해당 Error 코드에 대응하는 에러 응답
   */
  @ExceptionHandler(value = {CustomException.class})
  public ResponseEntity<?> handleCustomException(CustomException e) {
    log.error("handleCustomException() in GlobalExceptionHandler throw CustomException : {}",
        e.getMessage());
    return ResponseEntity
        .status(e.getErrorCode().getStatus())
        .body(ErrorResponse.of(e.getErrorCode(), e,e.getClass().getSimpleName()));
  }

  /**
   * 기본적인 예외에 대한 처리
   *
   * @param e
   * @return 500 INTERNAL SERVER ERROR 응답
   */
  @ExceptionHandler(value = {Exception.class})
  public ResponseEntity<?> handleException(Exception e) {
    log.error("handleCustomException() in GlobalExceptionHandler throw Exception : {}",
        e.getMessage());
    return ResponseEntity
        .status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
        .body(ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR, e,e.getClass().getSimpleName()));
  }

}
