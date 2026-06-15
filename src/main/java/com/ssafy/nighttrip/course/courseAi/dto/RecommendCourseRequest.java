package com.ssafy.nighttrip.course.courseAi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class RecommendCourseRequest {

    @NotBlank(message = "도시는 필수입니다.")
    private String city;

    @NotNull(message = "날짜는 필수입니다.")
    private LocalDate date;

    @NotBlank(message = "여행 요청 내용은 필수입니다.")
    private String content;
}