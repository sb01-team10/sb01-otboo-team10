package com.codeit.weatherwear.global.exception.s3;

import com.codeit.weatherwear.global.exception.CustomException;
import com.codeit.weatherwear.global.exception.ErrorCode;

public class S3UploadException extends CustomException {

  public S3UploadException() {
    super(ErrorCode.S3_UPLOAD_FAILED);
  }
}
