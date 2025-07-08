package com.codeit.weatherwear.domain.location.exception;

import com.codeit.weatherwear.global.exception.CustomException;
import com.codeit.weatherwear.global.exception.ErrorCode;
import java.util.Map;

public class KakaoGeoApiResponseException extends CustomException {

  public KakaoGeoApiResponseException(int status) {
    super(ErrorCode.KAKAO_GEO_API_RESPONSE_ERROR, Map.of("status", status));
  }
}
