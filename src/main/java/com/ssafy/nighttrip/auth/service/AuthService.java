package com.ssafy.nighttrip.auth.service;

import com.ssafy.nighttrip.auth.dto.LoginRequest;
import com.ssafy.nighttrip.auth.dto.LoginResponse;
import com.ssafy.nighttrip.global.exception.BusinessException;
import com.ssafy.nighttrip.global.exception.ErrorCode;
import com.ssafy.nighttrip.global.security.jwt.JwtTokenProvider;
import com.ssafy.nighttrip.user.domain.User;
import com.ssafy.nighttrip.user.mapper.UserMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    public LoginResult login(LoginRequest request) {
        User user = userMapper.findByEmail(request.getEmail());

        if (user == null) {
            throw new BusinessException(ErrorCode.LOGIN_FAILED);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.LOGIN_FAILED);
        }

        if (!"ACTIVE".equals(user.getStatus())) {
            throw new BusinessException(ErrorCode.USER_INACTIVE);
        }

        String accessToken = jwtTokenProvider.createAccessToken(
                user.getUserId(),
                user.getRole()
        );

        String refreshToken = jwtTokenProvider.createRefreshToken(user.getUserId());

        refreshTokenService.saveRefreshToken(
                user.getUserId(),
                refreshToken,
                jwtTokenProvider.getRefreshTokenExpirationMillis()
        );

        LoginResponse response = new LoginResponse(
                accessToken,
                "Bearer",
                jwtTokenProvider.getAccessTokenExpirationMillis()
        );

        return new LoginResult(response, refreshToken);
    }

    @Getter
    @RequiredArgsConstructor
    public static class LoginResult {
        private final LoginResponse response;
        private final String refreshToken;
    }
}