package com.ssafy.nighttrip.user.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class User {

    private Long userId;
    private String email;
    private String password;
    private String nickname;
    private String profileImageUrl;
    private String role;
    private String status;
    private String provider;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}