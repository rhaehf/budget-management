package com.management.budget.config;

import com.management.budget.user.domain.User;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class TokenProvider {
    @Value("${jwt.secret_key}")
    private String secretKey;
    @Value("${jwt.access_token_expiration}")
    private long accessTokenValidTime;
    @Value("${jwt.refresh_token_expiration}")
    private long refreshTokenValidTime;

    // 시크릿 키를 변환
    private Key getSigningKey() {
        return new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS512.getJcaName());
    }

    // accessToken 생성
    public String createAccessToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenValidTime);

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE) // header - "typ": "JWT"
                .setSubject(user.getUserId().toString()) // userId를 subject로 설정
                .setIssuedAt(now) // 발행 시간
                .setExpiration(expiryDate) // 만료 시간
                .signWith(getSigningKey(), SignatureAlgorithm.HS512) // 시크릿키, 해싱 알고리즘 - "alg": "HS512"
                .compact();
    }

    // refreshToken 생성
    public String createRefreshToken() {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenValidTime);

        return Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }
}
