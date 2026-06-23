package com.ssafy.nighttrip.auth.service;

import com.ssafy.nighttrip.global.exception.BusinessException;
import com.ssafy.nighttrip.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class SocialLoginCodeService {

    private static final String SOCIAL_LOGIN_CODE_PREFIX = "social:login:";
    private static final long EXPIRATION_SECONDS = 60;

    private final StringRedisTemplate stringRedisTemplate;

    public String createCode(Long userId) {
        String code = UUID.randomUUID().toString();

        stringRedisTemplate.opsForValue().set(
                getKey(code),
                userId.toString(),
                EXPIRATION_SECONDS,
                TimeUnit.SECONDS
        );

        return code;
    }

    public Long consumeCode(String code) {
        String userId = stringRedisTemplate.opsForValue()
                .getAndDelete(getKey(code));

        if (userId == null) {
            throw new BusinessException(ErrorCode.INVALID_SOCIAL_LOGIN_CODE);
        }

        return Long.valueOf(userId);
    }

    private String getKey(String code) {
        return SOCIAL_LOGIN_CODE_PREFIX + code;
    }
}