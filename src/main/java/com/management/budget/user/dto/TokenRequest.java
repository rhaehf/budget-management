package com.management.budget.user.dto;

import jakarta.validation.constraints.NotBlank;

public record TokenRequest(
        @NotBlank(message = "리프레시 토큰은 필수 값 입니다.") String refreshToken
) {
}
