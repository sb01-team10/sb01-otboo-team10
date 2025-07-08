package com.codeit.weatherwear.domain.weather.exception;

import com.codeit.weatherwear.global.exception.CustomException;
import com.codeit.weatherwear.global.exception.ErrorCode;

public class WeatherApiRequestException extends CustomException {

  public WeatherApiRequestException() {
    super(ErrorCode.WEATHER_API_REQUEST_ERROR);
  }
}
