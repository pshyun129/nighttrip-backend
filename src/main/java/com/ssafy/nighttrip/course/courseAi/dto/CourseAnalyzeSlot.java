package com.ssafy.nighttrip.course.courseAi.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class CourseAnalyzeSlot {

    private int number;
    private String category;
    private List<String> tags = new ArrayList<>();
    private List<String> banTags = new ArrayList<>();
}