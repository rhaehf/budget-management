package com.management.budget.user.dto;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}
