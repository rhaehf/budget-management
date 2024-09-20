package com.management.budget.user.repository;

import com.management.budget.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    // 계정으로 사용자 조회
    Optional<User> findByAccount(String account);
}
