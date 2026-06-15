package com.ssafy.nighttrip.course.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
public class Course {

    private Long courseId;
    private Long cityId;
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
}