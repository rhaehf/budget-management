package com.management.budget.exception.handler;

import com.management.budget.exception.BaseException;
import com.management.budget.exception.ErrorCode;
import com.management.budget.exception.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

    // @Valid(유효성 검증) 관련 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("handleMethodArgumentNotValidException throw MethodArgumentNotValidException: {}", e.getMessage(), e);
        String errorMessage = e.getBindingResult().getFieldError().getDefaultMessage();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ErrorCode.INVALID_INPUT_VALUE, errorMessage));
    }

    // JPA에서 엔티티를 못찾는 예외 처리
    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException e) {
        log.error("handleEntityNotFoundException throw EntityNotFoundException: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ErrorCode.ENTITY_NOT_FOUND, e.getMessage()));
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
