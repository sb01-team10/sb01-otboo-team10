package com.codeit.weatherwear.domain.user.exception;

import com.codeit.weatherwear.global.exception.CustomException;
import com.codeit.weatherwear.global.exception.ErrorCode;

public class UserNotFoundException extends CustomException {


    public UserNotFoundException() {
        super(ErrorCode.USER_NOT_FOUND);
    }
}
