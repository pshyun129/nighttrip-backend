package com.ssafy.nighttrip.course.courseAi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class RecommendCourseResponse {

    private String city;
    private LocalDate date;
    private List<RecommendedCourseDto> recommendations;
}