package com.ssafy.nighttrip.auth.controller;

import com.ssafy.nighttrip.auth.dto.LoginRequest;
import com.ssafy.nighttrip.auth.dto.LoginResponse;
import com.ssafy.nighttrip.auth.service.AuthService;
import com.ssafy.nighttrip.global.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletRequest request
    ) {
        AuthService.LoginResult loginResult = authService.login(loginRequest);

        ResponseCookie refreshTokenCookie = ResponseCookie.from(
                        "refreshToken",
                        loginResult.getRefreshToken()
                )
                .httpOnly(true)
                .secure(false) // local 개발 환경. 차후에 https를 사용할 때는 true 로 바꿔야함
                .sameSite("Lax")
                .path("/api/auth")
                .maxAge(Duration.ofDays(7))
                .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(ApiResponse.success(
                        HttpStatus.OK,
                        "로그인 성공",
                        loginResult.getResponse(),
                        request
                ));
    }
}