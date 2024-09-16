package com.management.budget.exception.handler;

import com.management.budget.exception.BaseException;
import com.management.budget.exception.ErrorCode;
import com.management.budget.exception.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    // Base Exception
    @ExceptionHandler(BaseException.class)
    protected ResponseEntity<ErrorResponse> handleBaseException(BaseException e) {
        log.error("handleBaseException throw BaseException: {}", e.getErrorCode(), e);
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(new ErrorResponse(e.getErrorCode(), e.getMessage()));
    }

    // hibernate 관련 에러 처리
    @ExceptionHandler(value = {ConstraintViolationException.class, DataIntegrityViolationException.class})
    protected ResponseEntity<ErrorResponse> handleDataException(final Exception e) {
        log.error("handleDataException throw Exception : {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.CONFLICT) // 데이터 충돌 오류
                .body(new ErrorResponse(ErrorCode.DUPLICATE_RESOURCE));
    }
}
