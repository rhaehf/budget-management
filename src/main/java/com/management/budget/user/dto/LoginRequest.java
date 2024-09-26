package com.management.budget.user.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "아이디는 필수 값 입니다.") String account,
        @NotBlank(message = "비밀번호는 필수 값 입니다.") String password
) {
}
