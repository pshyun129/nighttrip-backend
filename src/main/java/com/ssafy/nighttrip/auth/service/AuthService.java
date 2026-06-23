package com.ssafy.nighttrip.auth.service;

import com.ssafy.nighttrip.auth.dto.*;
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
    private final SocialLoginCodeService socialLoginCodeService;

    // 일반 로그인
    public LoginResult login(LoginRequest request) {
        User user = userMapper.findByEmail(request.getEmail());

        if (user == null) {
            throw new BusinessException(ErrorCode.LOGIN_FAILED);
        }

        if (!"LOCAL".equals(user.getProvider())) {
            throw new BusinessException(ErrorCode.GOOGLE_LOGIN_REQUIRED);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.LOGIN_FAILED);
        }


        return issueLogin(user);
    }

    // 구글 로그인
    public LoginResult exchangeGoogleLoginCode(String code) {
        Long userId = socialLoginCodeService.consumeCode(code);

        User user = userMapper.findById(userId);

        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        if (!"GOOGLE".equals(user.getProvider())) {
            throw new BusinessException(ErrorCode.INVALID_SOCIAL_LOGIN_CODE);
        }


        return issueLogin(user);
    }

    // 로그인 이후 토큰 처리
    private LoginResult issueLogin(User user) {
        String accessToken = jwtTokenProvider.createAccessToken(
                user.getUserId(),
                user.getRole()
        );

        String refreshToken = jwtTokenProvider.createRefreshToken(
                user.getUserId()
        );

        refreshTokenService.saveRefreshToken(
                user.getUserId(),
                refreshToken,
                jwtTokenProvider.getRefreshTokenExpirationMillis()
        );

        LoginResponse response = new LoginResponse(
                accessToken,
                "Bearer",
                jwtTokenProvider.getAccessTokenExpirationMillis(),
                LoginUserResponse.from(user)
        );

        return new LoginResult(response, refreshToken);
    }


    // 리프레시
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
                jwtTokenProvider.getAccessTokenExpirationMillis(),
                LoginUserResponse.from(user)
        );
    }

    // 로그아웃
    public void logout(Long userId) {

        refreshTokenService.deleteRefreshToken(userId);

    }


    // TODO

}