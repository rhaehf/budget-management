package com.management.budget.user.repository;

import com.management.budget.user.domain.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TokenRepository extends JpaRepository<Token, Long> {
    // userId로 Token 조회
    Optional<Token> findByUser_UserId(UUID userId);
}
