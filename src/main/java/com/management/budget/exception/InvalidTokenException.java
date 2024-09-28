package com.management.budget.exception;

public class InvalidTokenException extends BaseException {
    public InvalidTokenException(ErrorCode errorCode) {
        super(errorCode);
    }

    public InvalidTokenException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}