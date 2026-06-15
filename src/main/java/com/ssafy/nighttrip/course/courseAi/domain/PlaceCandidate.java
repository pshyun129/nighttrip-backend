package com.ssafy.nighttrip.course.courseAi.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaceCandidate {

    private Long placeId;
    private String name;
    private String category;
    private Double latitude;
    private Double longitude;

    private double preferredTagScore;
    private double banTagScore;

    private String matchedTags;
}