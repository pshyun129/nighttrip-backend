package com.ssafy.nighttrip.course.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
public class SaveCourseItemRequest {

    private Integer rank;

    // 현재 ERD에는 저장 컬럼이 없지만, AI 응답 그대로 받기 위해 둠
    private BigDecimal totalScore;
    private BigDecimal totalDistanceKm;

    private Integer estimatedMoveMinutes;

    @Valid
    @NotEmpty(message = "코스에는 최소 1개 이상의 장소가 포함되어야 합니다.")
    private List<SaveCoursePlaceRequest> places;
}