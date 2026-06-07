package com.ssafy.nighttrip.user.dto;

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
}
