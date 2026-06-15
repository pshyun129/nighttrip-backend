package com.ssafy.nighttrip.course.courseAi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RecommendedPlaceDto {

    private int order;
    private Long placeId;
    private String name;
    private String category;
    private Double latitude;
    private Double longitude;
    private double score;
    private double tagScore;
    private double distanceScore;
    private List<String> tags;
}