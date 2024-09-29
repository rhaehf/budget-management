package com.management.budget.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.management.budget.exception.ErrorCode;
import com.management.budget.exception.ErrorResponse;
import com.management.budget.exception.TokenException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        ErrorCode errorCode;
        final String errorMessage;

        // InvalidTokenException의 ErrorCode에 따라 다르게 처리
        if (authException instanceof TokenException) {
            TokenException tokenException = (TokenException) authException;
            errorCode = tokenException.getErrorCode();
            errorMessage = tokenException.getMessage() != null ? tokenException.getMessage() : errorCode.getMessage();
        } else {
            errorCode = ErrorCode.UNAUTHORIZED_ACCESS; // 기본 에러 코드
            errorMessage = errorCode.getMessage();  // 기본 메시지 사용
        }

        // 응답 상태 설정 및 JSON 응답 반환
        response.setStatus(errorCode.getStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponse = new ErrorResponse(errorCode, errorMessage);
        String jsonResponse = objectMapper.writeValueAsString(errorResponse);

        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
}
