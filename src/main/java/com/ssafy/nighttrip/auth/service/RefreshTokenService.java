package com.ssafy.nighttrip.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private static final String REFRESH_TOKEN_PREFIX = "refresh:user:";

    private final StringRedisTemplate stringRedisTemplate;

    // 리프래시 토큰 저장
    public void saveRefreshToken(Long userId, String refreshToken, long expirationMillis) {
        String key = getKey(userId);

        stringRedisTemplate.opsForValue().set(
                key,
                refreshToken,
                expirationMillis,
                TimeUnit.MILLISECONDS
        );
    }

    public String getRefreshToken(Long userId) {
        return stringRedisTemplate.opsForValue().get(getKey(userId));
    }

    // 리프래시 토큰 비교
    public boolean matches(Long userId, String refreshToken) {
        String savedRefreshToken = getRefreshToken(userId);
        return refreshToken.equals(savedRefreshToken);
    }

    public void deleteRefreshToken(Long userId) {
        stringRedisTemplate.delete(getKey(userId));
    }

    private String getKey(Long userId) {
        return REFRESH_TOKEN_PREFIX + userId;
    }
}