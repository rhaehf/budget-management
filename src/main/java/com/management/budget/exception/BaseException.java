package com.management.budget.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BaseException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String message;

    public BaseException(ErrorCode errorCode) {
        this(errorCode, errorCode.getMessage());
    }
}
