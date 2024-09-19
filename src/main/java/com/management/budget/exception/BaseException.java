package com.management.budget.exception;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {
    private final ErrorCode errorCode;

    public BaseException(ErrorCode errorCode) {
        this(errorCode, errorCode.getMessage()); // 밑에 생성자를 호출
    }

    public BaseException(ErrorCode errorCode, String message) {
        super(message); // 부모 클래스 RuntimeException의 message 필드를 설정
        this.errorCode = errorCode;
    }
}
