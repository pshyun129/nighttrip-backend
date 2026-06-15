package com.ssafy.nighttrip.course.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
public class SaveCourseRequest {

    @NotBlank(message = "도시는 필수입니다.")
    private String city;

    // 현재 course 테이블에는 travel_date 컬럼이 없으므로 저장에는 직접 사용하지 않음
    private LocalDate date;

    @Valid
    @NotEmpty(message = "저장할 코스는 최소 1개 이상이어야 합니다.")
    private List<SaveCourseItemRequest> courses;
}