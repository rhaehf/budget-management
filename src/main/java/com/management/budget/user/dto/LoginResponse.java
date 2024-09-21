package com.management.budget.user.dto;

public record LoginResponse(
        String accessToken,
        String refreshToken
) {
}
