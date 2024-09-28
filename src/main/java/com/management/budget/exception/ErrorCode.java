package com.management.budget.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // 공통
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    // 클라이언트의 입력 값에 대한 일반적인 오류 (@Valid를 통한 유효성 검증, @PathVariable, @RequestParam가 잘못되었을 때)
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "클라이언트의 입력 값을 확인해주세요."),
    // @RequestBody의 입력 값이 유효하지 않을 때
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "파라미터 값을 확인해주세요."),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 엔티티입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "허용되지 않은 메서드입니다."),
    DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "데이터가 이미 존재합니다"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버 오류입니다."),

    // 사용자 회원가입, 로그인
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호 형식에 맞지 않습니다."),

    // JWT 관련
    TOKEN_MISSING_USERID(HttpStatus.UNAUTHORIZED, "토큰에 userId가 포함되어 있지 않습니다."),
    Token_Expired(HttpStatus.UNAUTHORIZED,"JWT 토큰이 만료되었습니다."),
    ;

    private final HttpStatus status;
    private final String message;
}
