package com.ssafy.nighttrip.course.courseAi.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class CourseAnalyzeResponse {

    private String city;
    private LocalDate date;
    private String startPoint;
    private int categoryCount;
    private List<CourseAnalyzeSlot> data = new ArrayList<>();
}