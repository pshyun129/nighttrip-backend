package com.ssafy.nighttrip.course.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class UpdateCourseRequest {

    private Long cityId;

    @NotBlank(message = "코스 제목은 필수입니다.")
    @Size(max = 100, message = "코스 제목은 100자 이하이어야 합니다.")
    private String title;

    @NotBlank(message = "코스 설명은 필수입니다.")
    @Size(max = 100, message = "코스 설명은 100자 이하이어야 합니다.")
    private String description;

    private String theme;

    private LocalTime startTime;

    private LocalTime endTime;

    private Integer totalDurationMinutes;

    private Integer totalTravelMinutes;

    @NotBlank(message = "이동수단은 필수입니다.")
    @Size(max = 10, message = "이동수단은 10자 이하이어야 합니다.")
    private String transport;

    @Valid
    @NotEmpty(message = "코스에는 최소 1개 이상의 장소가 포함되어야 합니다.")
    private List<UpdateCoursePlaceRequest> places;
}