package com.ssafy.nighttrip.auth.dto;

import com.ssafy.nighttrip.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginUserResponse {

    private Long userId;
    private String email;
    private String nickname;
    private String role;
    private String profileImageUrl;

    public static LoginUserResponse from(User user) {
        return new LoginUserResponse(
                user.getUserId(),
                user.getEmail(),
                user.getNickname(),
                user.getRole(),
                user.getProfileImageUrl()
        );
    }
}