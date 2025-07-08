package com.codeit.weatherwear.domain.weather.exception;

import com.codeit.weatherwear.global.exception.CustomException;
import com.codeit.weatherwear.global.exception.ErrorCode;

public class WeatherApiParsingException extends CustomException {

  public WeatherApiParsingException() {
    super(ErrorCode.WEATHER_API_PARSING_FAILED);
  }
}
