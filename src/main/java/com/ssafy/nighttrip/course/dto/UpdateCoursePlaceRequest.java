package com.ssafy.nighttrip.course.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateCoursePlaceRequest {

    @NotNull(message = "장소 ID는 필수입니다.")
    private Long placeId;

    @NotNull(message = "장소 순서는 필수입니다.")
    private Integer sequence;

    private Integer travelMinutesFromPrevious;
}