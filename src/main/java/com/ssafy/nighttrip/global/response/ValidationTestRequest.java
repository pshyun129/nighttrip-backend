package com.ssafy.nighttrip.global.response;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ValidationTestRequest {
    @NotBlank(message = "이름은 필수입니다.")
    private String name;
}
