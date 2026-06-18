package com.ssafy.nighttrip.course.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class MyCourseListResponse {

    private Long courseId;
    private Long cityId;
    private String cityName;
    private String title;
    private String description;
    private String theme;
    private Integer totalDurationMinutes;
    private Integer totalTravelMinutes;
    private String transport;
    private Integer placeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}