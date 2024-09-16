package com.management.budget.exception;

import lombok.Getter;

@Getter
public class ErrorResponse {
    private final int status;
    private final String codeName;
    private final String message;

    public ErrorResponse(ErrorCode errorCode) {
        this.status = errorCode.getStatus().value();
        this.codeName = errorCode.name();
        this.message = errorCode.getMessage();
    }

    public ErrorResponse(ErrorCode errorCode, String customMessage) {
        this.status = errorCode.getStatus().value();
        this.codeName = errorCode.name();
        this.message = customMessage != null ? customMessage : errorCode.getMessage();
    }
}
