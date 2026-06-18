package com.ssafy.nighttrip.course.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MyCourseDetailResponse {

    private Long courseId;
    private Long cityId;
    private String cityName;
    private Long userId;
    private String title;
    private String description;
    private String theme;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer totalDurationMinutes;
    private Integer totalTravelMinutes;
    private String transport;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    private List<MyCoursePlaceResponse> places = new ArrayList<>();
}