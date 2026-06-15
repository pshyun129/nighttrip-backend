package com.ssafy.nighttrip.course.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SaveCourseResponse {

    private List<Long> courseIds;
}