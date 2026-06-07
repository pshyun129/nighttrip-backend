package com.ssafy.nighttrip.auth.service;

import com.ssafy.nighttrip.auth.dto.LoginRequest;
import com.ssafy.nighttrip.auth.dto.LoginResponse;
import com.ssafy.nighttrip.auth.dto.LoginResult;
import com.ssafy.nighttrip.auth.dto.RefreshResponse;
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

        // 일치하는 사용자가 없으면
        if (user == null) {
            throw new BusinessException(ErrorCode.LOGIN_FAILED);
        }

        // 패스워드가 일치하지 않으면
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.LOGIN_FAILED);
        }

        // 활성화 되지 않은 사용자라면
        if (!"ACTIVE".equals(user.getStatus())) {
            throw new BusinessException(ErrorCode.USER_INACTIVE);
        }

        // 토큰 생성
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

    public RefreshResponse refresh(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        if (!jwtTokenProvider.isRefreshToken(refreshToken)) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN_TYPE);
        }

        Long userId = jwtTokenProvider.getUserId(refreshToken);

        if (!refreshTokenService.matches(userId, refreshToken)) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_MISMATCH);
        }

        User user = userMapper.findById(userId);

        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        if (!"ACTIVE".equals(user.getStatus())) {
            throw new BusinessException(ErrorCode.USER_INACTIVE);
        }

        String newAccessToken = jwtTokenProvider.createAccessToken(
                user.getUserId(),
                user.getRole()
        );

        return new RefreshResponse(
                newAccessToken,
                "Bearer",
                jwtTokenProvider.getAccessTokenExpirationMillis()
        );
    }

    // 로그아웃
    public void logout(Long userId) {

        refreshTokenService.deleteRefreshToken(userId);

    }


}