package com.codeit.weatherwear.global.exception;

import com.codeit.weatherwear.global.response.ErrorResponse;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
    //TODO: 배포 후 e.getMessage()로 바꾸기
    log.error("GlobalExceptionHandler catch NoHandlerFoundException : {}", e);
    return ResponseEntity
        .status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
        .body(ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR, e,Map.of("reason", "No handler or unsupported method")));
  }

  /**
   * 커스텀 예외에 대한 처리.
   *
   * @param e
   * @return 해당 Error 코드에 대응하는 에러 응답
   */
  @ExceptionHandler
  public ResponseEntity<?> handleCustomException(CustomException e) {
    log.error("handleCustomException() in GlobalExceptionHandler throw CustomException : {}",
        e.getMessage());
    ErrorResponse errorResponse = ErrorResponse.of(e.getErrorCode(),e,
        e.getDetails());
    return ResponseEntity
        .status(e.getErrorCode().getStatus())
        .body(errorResponse);
  }

  /**
   * 기본적인 예외에 대한 처리
   *
   * @param e
   * @return 500 INTERNAL SERVER ERROR 응답
   */
  @ExceptionHandler
  public ResponseEntity<?> handleException(Exception e) {
    //TODO: 배포 후 e.getMessage()로 바꾸기
    log.error("handleCustomException() in GlobalExceptionHandler throw Exception : {}",
        e);
    return ResponseEntity
        .status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
        .body(ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR, e,Map.of("reason", "Unexpected error")));
  }

  // Validation 예외
  @ExceptionHandler
  public ResponseEntity<?> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    BindingResult bindingResult = e.getBindingResult();

    List<String> errors = bindingResult.getFieldErrors().stream()
        .map(error -> String.format("[field=%s, rejected=%s, message=%s]",
            error.getField(),
            error.getRejectedValue(),
            error.getDefaultMessage()))
        .toList();

    log.warn("Validation Failed: {}", errors);
    return ResponseEntity
        .status(ErrorCode.INVALID_INPUT_VALUE.getStatus())
        .body(ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE,e, Map.of("validationError", errors)));
  }


}
