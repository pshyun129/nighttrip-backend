package com.ssafy.nighttrip.auth.service;

import com.ssafy.nighttrip.global.exception.BusinessException;
import com.ssafy.nighttrip.global.exception.ErrorCode;
import com.ssafy.nighttrip.user.domain.User;
import com.ssafy.nighttrip.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class GoogleOAuthService {

    private static final String PROVIDER_LOCAL = "LOCAL";
    private static final String PROVIDER_GOOGLE = "GOOGLE";

    private final UserMapper userMapper;

    @Transactional
    public Long findOrCreateGoogleUser(
            String email,
            Boolean emailVerified,
            String googleName
    ) {
        if (email == null || email.isBlank() || !Boolean.TRUE.equals(emailVerified)) {
            throw new BusinessException(ErrorCode.GOOGLE_EMAIL_NOT_VERIFIED);
        }

        User user = userMapper.findByEmail(email);

        if (user == null) {
            return createGoogleUser(email, googleName);
        }

        if (PROVIDER_LOCAL.equals(user.getProvider())) {
            throw new BusinessException(ErrorCode.LOCAL_ACCOUNT_ALREADY_EXISTS);
        }

        if (!PROVIDER_GOOGLE.equals(user.getProvider())) {
            throw new BusinessException(ErrorCode.SOCIAL_LOGIN_FAILED);
        }

        if (!"ACTIVE".equals(user.getStatus())) {
            throw new BusinessException(ErrorCode.USER_INACTIVE);
        }

        return user.getUserId();
    }

    private Long createGoogleUser(String email, String googleName) {
        User user = new User();

        user.setEmail(email);
        user.setNickname(createAvailableNickname(googleName, email));
        user.setRole("USER");
        user.setStatus("ACTIVE");
        user.setProvider(PROVIDER_GOOGLE);

        int inserted = userMapper.insertGoogleUser(user);

        if (inserted != 1 || user.getUserId() == null) {
            throw new BusinessException(ErrorCode.SOCIAL_LOGIN_FAILED);
        }

        return user.getUserId();
    }

    private String createAvailableNickname(String googleName, String email) {
        String base = googleName;

        if (base == null || base.isBlank()) {
            int atIndex = email.indexOf("@");
            base = atIndex > 0 ? email.substring(0, atIndex) : "googleUser";
        }

        base = base.trim().replaceAll("\\s+", " ");

        for (int i = 0; i < 20; i++) {
            String suffix = String.valueOf(
                    ThreadLocalRandom.current().nextInt(1000, 10000)
            );

            int maxBaseLength = 50 - suffix.length() - 1;

            String nicknameBase = base.length() > maxBaseLength
                    ? base.substring(0, maxBaseLength)
                    : base;

            String candidate = nicknameBase + "_" + suffix;

            if (userMapper.countByNickname(candidate) == 0) {
                return candidate;
            }
        }

        throw new BusinessException(ErrorCode.SOCIAL_LOGIN_FAILED);
    }
}