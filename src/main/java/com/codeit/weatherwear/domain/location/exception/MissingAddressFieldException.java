package com.codeit.weatherwear.domain.location.exception;

import com.codeit.weatherwear.global.exception.CustomException;
import com.codeit.weatherwear.global.exception.ErrorCode;

public class MissingAddressFieldException extends CustomException {

  public MissingAddressFieldException() {
    super(ErrorCode.ADDRESS_FIELD_NOT_FOUND);
  }
}
