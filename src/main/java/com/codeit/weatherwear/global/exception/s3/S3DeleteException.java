package com.codeit.weatherwear.global.exception.s3;

import com.codeit.weatherwear.global.exception.CustomException;
import com.codeit.weatherwear.global.exception.ErrorCode;

public class S3DeleteException extends CustomException {

  public S3DeleteException() {
    super(ErrorCode.S3_DELETE_FAILED);
  }
}
