package com.ssafy.nighttrip.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// 업로드 완료 후 저장 요청
public record UpdateProfileImageRequest(
        @NotBlank(message = "publicId는 필수입니다.")
        String publicId,

        @NotBlank(message = "이미지 URL은 필수입니다.")
        String imageUrl,

        @NotNull(message = "version은 필수입니다.")
        Long version,

        @NotBlank(message = "signature는 필수입니다.")
        String signature
) {
}