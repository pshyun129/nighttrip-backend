package com.ssafy.nighttrip.course.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CoursePlace {

    private Long coursePlaceId;
    private Long courseId;
    private Long placeId;
    private Integer sequence;
    private Integer travelMinutesFromPrevious;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}