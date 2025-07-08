package com.codeit.weatherwear.domain.location.exception;

import com.codeit.weatherwear.global.exception.CustomException;
import com.codeit.weatherwear.global.exception.ErrorCode;

public class AddressJsonParseException extends CustomException {

  public AddressJsonParseException() {
    super(ErrorCode.ADDRESS_JSON_PARSE_ERROR);
  }
}
