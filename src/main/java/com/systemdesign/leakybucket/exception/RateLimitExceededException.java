package com.systemdesign.leakybucket.exception;

import com.systemdesign.leakybucket.common.exception.BusinessException;
import com.systemdesign.leakybucket.common.exception.ExceptionCode;

public class RateLimitExceededException extends BusinessException {

    public RateLimitExceededException(ExceptionCode exceptionCode, Object... rejectedValues) {
        super(exceptionCode, rejectedValues);
    }
}
