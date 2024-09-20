package com.management.budget.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignUpRequest(
        @NotBlank(message = "아이디는 필수 값 입니다.") @Size(max = 50) String account,
        @NotBlank(message = "비밀번호는 필수 값 입니다.") @Size(min = 8, max = 100, message = "비밀번호는 최소 8자리 이상이어야 합니다.") String password
) {
}
