package com.codeit.weatherwear.domain.weather.exception;

import com.codeit.weatherwear.global.exception.CustomException;
import com.codeit.weatherwear.global.exception.ErrorCode;
import java.util.Map;

public class WeatherApiResponseException extends CustomException {

  public WeatherApiResponseException(String resultCode) {
    super(ErrorCode.WEATHER_API_RESPONSE_ERROR, Map.of("code", resultCode));
  }
}
