package com.codeit.weatherwear.domain.location.exception;

import com.codeit.weatherwear.global.exception.CustomException;
import com.codeit.weatherwear.global.exception.ErrorCode;

public class KakaoGeoApiRequestException extends CustomException {

  public KakaoGeoApiRequestException() {
    super(ErrorCode.KAKAO_GEO_API_REQUEST_ERROR);
  }
}
