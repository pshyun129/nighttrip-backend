package com.ssafy.nighttrip.user.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateMyInfoRequest {

    @NotBlank(message = "닉네임 입력은 필수입니다")
    private String nickname;

    // 나중에 이미지 처리 방식 확정나면 추가 구현
//  private String profileImageUrl;

}
