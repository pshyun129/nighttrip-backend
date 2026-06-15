package com.ssafy.nighttrip.review.analysis.dto;

import java.util.List;

public record ReviewAnalysisPrompt(
        List<ReviewItem> reviews
) {

    public record ReviewItem(
            Long reviewId,
            Long placeId,
            String placeName,
            String content,
            List<TagItem> allowedTags
    ) {
    }

    public record TagItem(
            Long tagId,
            String tagName
    ) {
    }
}