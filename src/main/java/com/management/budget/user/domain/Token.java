package com.management.budget.user.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table(name = "tokens")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long tokenId; // 토큰 일렬번호

    @Column(nullable = false)
    private String refreshToken; // 리프레시 토큰

    @OneToOne(fetch = FetchType.LAZY) // 사용자와 일대일 관계 설정
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 사용자

    // 리프레시 토큰 업데이트
    public void updateRefreshToken(String newToken) {
        this.refreshToken = newToken;
    }
}
