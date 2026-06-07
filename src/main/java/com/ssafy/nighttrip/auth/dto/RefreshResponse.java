package com.ssafy.nighttrip.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RefreshResponse {

    private String accessToken;
    private String tokenType;
    private long expiresIn;
}