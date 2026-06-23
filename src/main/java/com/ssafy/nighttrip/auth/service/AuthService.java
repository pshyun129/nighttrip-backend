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


    // 회원가입
    public SignUpResponse SignUp(SignUpRequest request) {
        if (userMapper.existsByEmail(request.getEmail()) > 0) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        if (userMapper.existsByNickname(request.getNickname()) > 0) {
            throw new BusinessException(ErrorCode.NICKNAME_ALREADY_EXISTS);
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(encodedPassword);
        user.setNickname(request.getNickname());
        user.setRole("USER");
        user.setStatus("ACTIVE");
        user.setProfileImageUrl(null);

        userMapper.insertUser(user);

        return SignUpResponse.from(user);
    }

    // 이메일 중복 체크
    public EmailCheckResponse EmailCheck(String email) {

        if(email == null || email.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        EmailCheckResponse response = new EmailCheckResponse(true);

        // 중복인게 있으면
        if(userMapper.existsByEmail(email) > 0) {
            response.setAvailable(false);
        }

        return response;
    }

    // 이메일 중복 체크
    public NicknameCheckResponse NicknameCheck(String nickname) {

        if(nickname == null || nickname.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        NicknameCheckResponse response = new NicknameCheckResponse(true);

        // 중복인게 있으면
        if(userMapper.existsByNickname(nickname) > 0) {
            response.setAvailable(false);
        }

        return response;
    }


}