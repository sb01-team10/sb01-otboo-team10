package com.codeit.weatherwear.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Custom Error 지정 필요성이 있다면 에러 코드를 작성하여 진행.
 * <p>
 * 일반적인 상황의 에러만 적혀있지만, 특정 도메인에서 발생할 수 있는 에러 등 정의\n\n
 * CustomException(ErrorCode.INTERNAL_SERVER_ERROR) 처럼 사용.
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {
  // COMMON
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러입니다.", "관리자에게 연락해 주세요."),
  INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.", "잘못된 요청을 진행하였습니다."),
  AUTHORIZATION_DENIED(HttpStatus.FORBIDDEN, "권한이 없습니다.", ""),

  // USER
  USER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "사용자 등록 실패", "사용자가 이미 존재합니다."),
  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자 확인 실패", "존재하지 않는 사용자입니다."),

  //FOLLOW
  SELF_FOLLOW_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "자기 자신을 팔로우 할 수 없습니다.", ""),
  FOLLOW_DUPLICATED(HttpStatus.BAD_REQUEST, "이미 팔로우한 사용자입니다.", ""),

  // FEED
  FEED_NOT_FOUND(HttpStatus.NOT_FOUND, "피드 조회 실패", "존재하지 않는 피드입니다."),
  UNSUPPORTED_SORT_FIELD(HttpStatus.BAD_REQUEST, "지원하지 않는 정렬 필드", "지원하지 않는 정렬 필드입니다."),
  NOT_IMPLEMENTED_SORT_FIELD(HttpStatus.NOT_IMPLEMENTED, "구현되지 않은 정렬 필드", "아직 구현되지 않은 정렬 필드입니다."),
  INVALID_ENUM_VALUE(HttpStatus.BAD_REQUEST, "잘못된 Enum 필드", "잘못된 Enum 필드입니다."),

  // WEATHER
  INVALID_WIND_SPEED(HttpStatus.BAD_REQUEST, "유효하지 않은 풍속", "풍속은 0 이상의 값이어야 합니다."),

  // JWT
  JWTSESSION_NOT_FOUND(HttpStatus.UNAUTHORIZED, "인증 정보 확인 실패", "토큰이 만료되거나 로그아웃되었습니다."),
  INVALID_JWT(HttpStatus.UNAUTHORIZED, "유효하지 않은 JWT", "JWT 토큰이 손상되었거나 유효하지 않습니다."),

  //ATTRIBUTE
  ATTRIBUTE_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "속성 등록 실패", "속성이 이미 존재합니다."),
  ATTRIBUTE_NOT_FOUND(HttpStatus.NOT_FOUND, "속성 확인 실패", "존재하지 않는 속성입니다."),
  SELECTABLE_DUPLICATE(HttpStatus.BAD_REQUEST, "속성 값 중복 등록", "중복된 속성 값이 존재합니다."),
  INVALID_ATTRIBUTE_NAME(HttpStatus.BAD_REQUEST, "잘못된 속성명입니다.", "정보를 찾을 수 없습니다."),

  //S3
  S3_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S3 이미지 업로드에 실패했습니다.", "이미지 저장 중 오류 발생"),
  PRESIGNED_URL_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Presigned URL 생성 실패", "이미지 접근 URL 생성 중 오류 발생"),
  S3_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S3 객체 삭제에 실패했습니다.", "이미지를 삭제할 수 없습니다."),


  //CLOTH
  CLOTH_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "옷 등록 실패", "존재하는 옷입니다."),
  CLOTH_NOT_FOUND(HttpStatus.NOT_FOUND, "옷 확인 실패", "존재하지 않는 옷입니다.");

  private final HttpStatus status;
  private final String message;
  private final String detail;
}
