package com.management.budget.user.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Table(name = "users")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, length = 36)
    private UUID userId; // 사용자 일렬번호

    @Column(unique = true, nullable = false)
    private String account; // 계정

    @Column(nullable = false)
    private String password; // 비밀번호
}
