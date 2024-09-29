package com.management.budget.config;

import com.management.budget.exception.ErrorCode;
import com.management.budget.exception.TokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
@Slf4j
public class TokenProvider {
    @Value("${jwt.secret_key}")
    private String secretKey;
    @Value("${jwt.access_token_expiration}")
    private long accessTokenValidTime;
    @Value("${jwt.refresh_token_expiration}")
    private long refreshTokenValidTime;

    // 서명에 사용할 키
    private Key getSigningKey() {
        // secretKey 를 UTF-8로 인코딩한 후, HS512 해싱 알고리즘에 맞는 SecretKeySpec 객체 생성
        return new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS512.getJcaName());
    }

    // accessToken 생성
    public String createAccessToken(UUID userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenValidTime);

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE) // 헤더 설정 ("typ": "JWT")
                .setSubject(userId.toString()) // userId를 subject로 설정
                .setIssuedAt(now) // 발행 시간
                .setExpiration(expiryDate) // 만료 시간
                .signWith(getSigningKey(), SignatureAlgorithm.HS512) // 서명 설정 ("alg": "HS512")
                .compact();
    }

    // refreshToken 생성
    public String createRefreshToken() {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenValidTime);

        return Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512) // 서명 알고리즘과 키 설정
                .compact();
    }

    // 토큰에서 userId 추출
    public UUID getUserIdFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey()) // 서명 키 설정
                .build()
                .parseClaimsJws(token) // 토큰의 서명 및 만료 시간 검증
                .getBody(); // 페이로드를 가져옴

        String userId = claims.getSubject();
        if (userId == null || userId.isEmpty()) {
            throw new TokenException(ErrorCode.TOKEN_MISSING_USERID);
        }

        return UUID.fromString(userId); // userId를 UUID로 변환
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            log.info("JWT 토큰이 비어 있거나 null입니다.");
            throw new TokenException(ErrorCode.INVALID_TOKEN, "JWT 토큰이 비어 있거나 null입니다.");
        }

        try {
            // 토큰을 파싱하고 서명을 검증
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey()) // 서명 검증을 위한 서명 키 설정
                    .build()
                    .parseClaimsJws(token); // 토큰의 서명 및 만료 시간 검증
            return true;
        } catch (JwtException e) {
            handleJwtException(e); // 세부적으로 예외 처리
        }
        return false;
    }

    // JwtException의 세부 예외 처리
    private void handleJwtException(JwtException e) {
        log.error("handleJwtException throw JwtException : {}", e.getMessage(), e);

        if (e instanceof ExpiredJwtException) {
            // 클라이언트가 만료된 JWT 토큰으로 요청을 보낼 때 발생
            // JWT에는 exp(만료 시간) 필드가 있으며, 이 시간을 초과한 경우 인증이 거부됨
            log.info("JWT 토큰이 만료되었습니다.");
            throw new TokenException(ErrorCode.TOKEN_EXPIRED);
        } else if (e instanceof SecurityException) {
            // 서명이 조작되었거나, 클라이언트가 제공한 JWT 서명이 서명 키와 일치하지 않을 때 발생
            // 예를 들어, 해커가 JWT의 서명 부분을 임의로 수정한 경우에 이 예외가 발생
            log.info("JWT 토큰의 서명이 잘못되었습니다.");
            throw new TokenException(ErrorCode.INVALID_SIGNATURE);
        } else if (e instanceof MalformedJwtException) {
            // 예를 들어, 헤더, 페이로드, 서명이 올바르게 분리되지 않거나 필요한 필드가 누락된 경우에 이 예외가 발생
            log.info("JWT 토큰의 구조가 잘못되었습니다.");
            throw new TokenException(ErrorCode.MALFORMED_TOKEN);
        } else if (e instanceof UnsupportedJwtException) {
            // 특정 형식의 JWT가 지원되지 않거나 애플리케이션이 기대하지 않는 형식의 JWT를 수신할 때 발생
            // 예를 들어, 특정 알고리즘을 지원하지 않는 경우에 이 예외가 발생
            log.info("지원되지 않는 형식의 JWT 토큰입니다.");
            throw new TokenException(ErrorCode.UNSUPPORTED_TOKEN);
        } else {
            // 잘못된 인수가 메서드에 전달되었을 때 발생 (JWT가 null 이거나 빈 값일 때, 혹은 잘못된 형태로 전달된 경우)
            // 예를 들어, 토큰이 없는 상태에서 검증을 시도했을 때 발생
            log.info("JWT 토큰에 잘못된 값이 포함되었습니다.");
            throw new TokenException(ErrorCode.TOKEN_ILLEGAL_ARGUMENT);
        }
    }
}
