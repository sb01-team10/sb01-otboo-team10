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

    // USER
    USER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "사용자 등록 실패", "사용자가 이미 존재합니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자 확인 실패", "존재하지 않는 사용자입니다."),

    //FOLLOW
    SELF_FOLLOW_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "자기 자신을 팔로우 할 수 없습니다.", ""),
    FOLLOW_DUPLICATED(HttpStatus.BAD_REQUEST, "이미 팔로우한 사용자입니다.", ""),

    // FEED
    FEED_NOT_FOUND(HttpStatus.NOT_FOUND, "피드 조회 실패", "존재하지 않는 피드입니다."),

    // WEATHER
    INVALID_WIND_SPEED(HttpStatus.BAD_REQUEST, "유효하지 않은 풍속", "풍속은 0 이상의 값이어야 합니다.");

    private final HttpStatus status;
    private final String message;
    private final String detail;
}
