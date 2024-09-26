package com.management.budget.user.service;

import com.management.budget.config.TokenProvider;
import com.management.budget.exception.BadRequestException;
import com.management.budget.exception.BaseException;
import com.management.budget.exception.ErrorCode;
import com.management.budget.user.domain.Token;
import com.management.budget.user.domain.User;
import com.management.budget.user.dto.LoginRequest;
import com.management.budget.user.dto.LoginResponse;
import com.management.budget.user.dto.SignUpRequest;
import com.management.budget.user.dto.SignUpResponse;
import com.management.budget.user.repository.TokenRepository;
import com.management.budget.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final TokenProvider tokenProvider;

    // 사용자 회원가입
    public SignUpResponse signUp(SignUpRequest request) {
        // 1. 계정 중복 확인
        if (userRepository.findByAccount(request.account()).isPresent()) {
            throw new BaseException(ErrorCode.DUPLICATE_RESOURCE, "이미 존재하는 아이디가 있습니다.");
        } else {
            // 2. 비밀번호 검사
            verifyPassword(request.password());
        }
        // 3. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.password());
        // 4. 사용자 만들기 및 저장
        User user = User.builder()
                .account(request.account())
                .password(encodedPassword)
                .build();
        userRepository.save(user);

        return new SignUpResponse(user.getUserId(), user.getAccount());
    }

    // 비밀번호 검사
    private void verifyPassword(String password) {
        // - 최소 8자리 이상이어야 한다.
        if (password.length() < 8) {
            throw new BadRequestException(ErrorCode.INVALID_PASSWORD, "비밀번호는 최소 8자리 이상이어야 합니다.");
        }
        // - 공백을 포함하거나 숫자로만 이루어질 수 없다.
        /** 아래의 조건을 모두 확인한다.
         * 공백 문자열 (빈 문자열 포함)
         * 숫자만 있는 문자열
         * 숫자와 공백(띄어쓰기, 탭, 줄 바꿈 등)이 섞인 문자열
         */
        if (password.matches("^[\\d\\s]*$")) {
            throw new BadRequestException(ErrorCode.INVALID_PASSWORD, "비밀번호는 공백을 포함하거나 숫자로만 이루어질 수 없습니다.");
        }
        // - 숫자, 문자, 특수문자 중 2가지 이상을 포함해야 한다.
        if (!(
                (password.matches(".*[a-zA-Z].*") && password.matches(".*\\d.*")) ||  // 문자와 숫자
                        (password.matches(".*[a-zA-Z].*") && password.matches(".*[!@#$%^&*].*")) ||  // 문자와 특수문자
                        (password.matches(".*\\d.*") && password.matches(".*[!@#$%^&*].*"))  // 숫자와 특수문자
        )) {
            throw new BadRequestException(ErrorCode.INVALID_PASSWORD, "비밀번호는 숫자, 문자, 특수문자 중 두 가지 이상을 포함해야 합니다.");
        }
    }

    // 사용자 로그인
    public LoginResponse login(LoginRequest request) {
        // 1. 계정으로 사용자 조회
        User user = userRepository.findByAccount(request.account())
                .orElseThrow(() -> new EntityNotFoundException("아이디가 존재하지 않습니다."));
        // 2. 비밀번호 일치 확인
        if (!passwordEncoder.matches(request.password(), user.getPassword()))
            throw new BadRequestException(ErrorCode.INVALID_PASSWORD, "비밀번호가 일치하지 않습니다.");

        // 3. refreshToken 이 있는지 확인
        Optional<Token> existingRefreshToken = tokenRepository.findByUser_UserId(user.getUserId());
        String newRefreshToken = tokenProvider.createRefreshToken();

        if (existingRefreshToken.isPresent()) { // refreshToken 이 있는 경우
            // 새로운 refreshToken 으로 업데이트
            existingRefreshToken.get().updateRefreshToken(newRefreshToken);
        } else {
            // refreshToken 이 없으면 DB에 저장
            tokenRepository.save(Token.builder()
                    .refreshToken(newRefreshToken)
                    .user(user)
                    .build());
        }

        // 4. accessToken, refreshToken 반환
        String accessToken = tokenProvider.createAccessToken(user);
        return new LoginResponse(accessToken, newRefreshToken);
    }
}
