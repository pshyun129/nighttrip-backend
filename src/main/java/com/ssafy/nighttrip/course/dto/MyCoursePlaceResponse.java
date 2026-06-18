package com.ssafy.nighttrip.course.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class MyCoursePlaceResponse {

    private Long coursePlaceId;
    private Long courseId;
    private Long placeId;
    private Integer sequence;
    private Integer travelMinutesFromPrevious;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String placeName;
    private BigDecimal latitude;
    private BigDecimal longitude;
}