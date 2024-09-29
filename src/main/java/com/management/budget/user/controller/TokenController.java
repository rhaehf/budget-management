package com.management.budget.user.controller;

import com.management.budget.user.dto.TokenRequest;
import com.management.budget.user.dto.TokenResponse;
import com.management.budget.user.service.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tokens")
@RequiredArgsConstructor
public class TokenController {
    private final TokenService tokenService;

    // AccessToken 재발급
    @PostMapping("/reissue")
    public ResponseEntity<TokenResponse> reissue(@Valid @RequestBody TokenRequest request) {
        TokenResponse response = tokenService.reissueAccessToken(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
