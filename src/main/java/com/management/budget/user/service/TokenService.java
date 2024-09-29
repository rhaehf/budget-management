package com.management.budget.user.service;

import com.management.budget.config.TokenProvider;
import com.management.budget.exception.ErrorCode;
import com.management.budget.exception.InvalidTokenException;
import com.management.budget.user.domain.Token;
import com.management.budget.user.dto.TokenRequest;
import com.management.budget.user.dto.TokenResponse;
import com.management.budget.user.repository.TokenRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TokenService {
    private final TokenProvider tokenProvider;
    private final TokenRepository tokenRepository;

    // AccessToken 재발급
    // 만료된 AccessToken 과 유효한 RefreshToken 으로 테스트 해야함. 만료된 RefreshToken 을 보낼 경우 재발급이 불가능
    public TokenResponse reissueAccessToken(TokenRequest request) {
        // 1. refreshToken 만료 확인
        if (!tokenProvider.validateToken(request.refreshToken())) {
            throw new InvalidTokenException(ErrorCode.INVALID_REFRESHTOKEN);
        }

        // 2. request의 refreshToken 으로 DB에서 token 조회
        Token dBToken = tokenRepository.findByRefreshToken(request.refreshToken())
                .orElseThrow(() -> new EntityNotFoundException("DB에 사용자의 토큰이 없습니다."));

        // 3. token에서 userId 추출
        UUID userId = dBToken.getUser().getUserId();

        // 4. accessToken 및 refreshToken 재발급
        String accessToken = tokenProvider.createAccessToken(userId);
        String refreshToken = tokenProvider.createRefreshToken();

        // 5. 새로 발급된 refreshToken 으로 DB에 업데이트
        dBToken.updateRefreshToken(refreshToken);

        // 6. 재발급된 accessToken 및 refreshToken 반환
        return new TokenResponse(accessToken, refreshToken);
    }
}
