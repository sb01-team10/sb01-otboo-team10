package com.codeit.weatherwear.domain.location.exception;

import com.codeit.weatherwear.global.exception.CustomException;
import com.codeit.weatherwear.global.exception.ErrorCode;
import java.util.Map;

public class LocationNotFoundException extends CustomException {

  public LocationNotFoundException(double latitude, double longitude) {
    super(ErrorCode.LOCATION_NOT_FOUND_BY_GEO_POINT,
        Map.of("latitude", latitude, "longitude", longitude));
  }
}
