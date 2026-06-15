package com.ssafy.nighttrip.course.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
public class SaveCoursePlaceRequest {

    @NotNull(message = "장소 순서는 필수입니다.")
    private Integer order;

    @NotNull(message = "장소 ID는 필수입니다.")
    private Long placeId;

    // 아래 필드들은 저장에는 사용하지 않지만, AI 응답 그대로 받아도 에러 안 나게 둠
    private String name;
    private String category;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private BigDecimal score;
    private BigDecimal tagScore;
    private BigDecimal distanceScore;
    private List<String> tags;
}