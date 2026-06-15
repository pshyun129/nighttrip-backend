package com.ssafy.nighttrip.course.courseAi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RecommendedCourseDto {

    private int rank;
    private double totalScore;
    private double totalDistanceKm;
    private int estimatedMoveMinutes;
    private List<RecommendedPlaceDto> places;
}