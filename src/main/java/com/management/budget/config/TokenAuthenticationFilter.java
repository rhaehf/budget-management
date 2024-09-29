package com.management.budget.config;

import com.management.budget.exception.TokenException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@RequiredArgsConstructor
// 인증 없이 허용되는 경로를 제외한 모든 Request 요청은 이 필터를 거치기 때문에 토큰 정보가 없거나 유효하지 않으면 정상적으로 처리되지 않음
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    // 요청이 들어올 때마다 실행되는 필터. 여기서 JWT를 추출하고 검증 후 SecurityContext에 인증 정보(여기서는 userId)를 설정함
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. Authorization 헤더에서 토큰 추출
        String token = getTokenFromRequest(request);
        try {
            // 2. JWT 유효성 검증
            if (token != null && tokenProvider.validateToken(token)) {
                // 3. JWT에서 userId 추출
                UUID userId = tokenProvider.getUserIdFromJWT(token);

                // 4. 추출된 userId로 인증 객체(UsernamePasswordAuthenticationToken) 생성
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userId, null, null);
                // 5. 사용자 인증 정보에 추가적인 세부 정보 설정
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 6. SecurityContext에 인증 정보를 설정하여 이후의 요청에서 인증된 사용자로 인식되도록 함
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (TokenException e) {
            // 예외 발생 시, AuthenticationEntryPoint로 위임하여 처리
            jwtAuthenticationEntryPoint.commence(request, response, e);
            return;
        }

        // 7. 요청을 다음 필터로 전달 (필터 체인을 계속 진행)
        filterChain.doFilter(request, response);
    }

    // Authorization 헤더에서 Bearer 토큰 추출
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7); // "Bearer " 이후 토큰 부분 추출
        }
        // Authorization 헤더에 유효한 토큰이 없으면 null 반환
        return null;
    }
}
