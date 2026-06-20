package com.ssafy.nighttrip.user.dto;

import com.ssafy.nighttrip.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MyInfoResponse {
    private Long userId;
    private String email;
    private String nickname;
    private String role;
    private String profileImageUrl;
    private LocalDateTime createdAt;

    public static MyInfoResponse from(User user) {
        return new MyInfoResponse(
                user.getUserId(),
                user.getEmail(),
                user.getNickname(),
                String.valueOf(user.getRole()),
                user.getProfileImageUrl(),
                user.getCreatedAt()
        );
    }
}
