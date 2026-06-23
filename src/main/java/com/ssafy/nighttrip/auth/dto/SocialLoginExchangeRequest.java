package com.ssafy.nighttrip.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SocialLoginExchangeRequest {

    @NotBlank(message = "소셜 로그인 코드가 필요합니다.")
    private String code;
}