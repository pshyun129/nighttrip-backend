package com.ssafy.nighttrip.auth.dto;

import com.ssafy.nighttrip.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignUpResponse {

    private Long userId;
    private String email;
    private String nickname;

    public static SignUpResponse from(User user) {
        return new SignUpResponse(
                user.getUserId(),
                user.getEmail(),
                user.getNickname()
        );
    }
}