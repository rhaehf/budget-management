package com.management.budget.user.dto;

import java.util.UUID;

public record SignUpResponse(
        UUID userId,
        String account
) {
}
