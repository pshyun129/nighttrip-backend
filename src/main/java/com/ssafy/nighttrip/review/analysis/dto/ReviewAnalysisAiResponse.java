package com.ssafy.nighttrip.review.analysis.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ReviewAnalysisAiResponse {

    private List<ReviewResult> results;

    @Getter
    @NoArgsConstructor
    public static class ReviewResult {

        private Long reviewId;
        private Long placeId;
        private List<TagResult> tags;
    }

    @Getter
    @NoArgsConstructor
    public static class TagResult {

        private Long tagId;
        private String sentiment;
    }
}