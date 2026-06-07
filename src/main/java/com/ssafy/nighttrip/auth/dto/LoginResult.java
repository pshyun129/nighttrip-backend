package com.ssafy.nighttrip.auth.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LoginResult {
    private final LoginResponse response;
    private final String refreshToken;
}