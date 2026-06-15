package com.ssafy.nighttrip.review.analysis.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ReviewAnalysisTarget {

    private Long reviewId;
    private Long placeId;
    private String placeName;
    private String content;
    private LocalDateTime createdAt;
}