package com.ssafy.nighttrip.course.courseAi.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RoutePlace {

    private int order;
    private Long placeId;
    private String name;
    private String category;
    private Double latitude;
    private Double longitude;

    private double score;
    private double tagScore;
    private double distanceScore;
    private double distanceFromPreviousKm;

    private List<String> tags;
}