package com.ssafy.nighttrip.review.analysis.service;

import com.ssafy.nighttrip.review.analysis.domain.PlaceTagCandidate;
import com.ssafy.nighttrip.review.analysis.domain.ReviewAnalysisTarget;
import com.ssafy.nighttrip.review.analysis.dto.ReviewAnalysisAiResponse;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Component
public class ReviewAnalysisValidator {

    private static final Set<String> ALLOWED_SENTIMENTS =
            Set.of("POSITIVE", "NEGATIVE");

    public void validate(
            List<ReviewAnalysisTarget> reviews,
            List<PlaceTagCandidate> candidates,
            ReviewAnalysisAiResponse response
    ) {
        if (response == null || response.getResults() == null) {
            throw new IllegalStateException(
                    "GMS 분석 결과가 비어 있습니다."
            );
        }

        Map<Long, ReviewAnalysisTarget> reviewById =
                createReviewMap(reviews);

        Map<Long, Set<Long>> allowedTagsByPlace =
                createAllowedTagMap(candidates);

        Set<Long> returnedReviewIds = new HashSet<>();

        for (ReviewAnalysisAiResponse.ReviewResult result
                : response.getResults()) {

            ReviewAnalysisTarget review =
                    reviewById.get(result.getReviewId());

            if (review == null) {
                throw new IllegalStateException(
                        "요청하지 않은 reviewId입니다: "
                                + result.getReviewId()
                );
            }

            if (!Objects.equals(
                    review.getPlaceId(),
                    result.getPlaceId()
            )) {
                throw new IllegalStateException(
                        "리뷰의 placeId가 일치하지 않습니다. reviewId="
                                + result.getReviewId()
                );
            }

            if (!returnedReviewIds.add(result.getReviewId())) {
                throw new IllegalStateException(
                        "reviewId가 중복되었습니다: "
                                + result.getReviewId()
                );
            }

            if (result.getTags() == null) {
                throw new IllegalStateException(
                        "tags는 null일 수 없습니다."
                );
            }

            validateTags(
                    result,
                    allowedTagsByPlace.getOrDefault(
                            result.getPlaceId(),
                            Set.of()
                    )
            );
        }

        if (!returnedReviewIds.equals(reviewById.keySet())) {
            throw new IllegalStateException(
                    "GMS 응답에서 일부 리뷰가 누락되었습니다."
            );
        }
    }

    private void validateTags(
            ReviewAnalysisAiResponse.ReviewResult result,
            Set<Long> allowedTagIds
    ) {
        Set<Long> returnedTagIds = new HashSet<>();

        for (ReviewAnalysisAiResponse.TagResult tag
                : result.getTags()) {

            if (!allowedTagIds.contains(tag.getTagId())) {
                throw new IllegalStateException(
                        "허용되지 않은 tagId입니다: "
                                + tag.getTagId()
                );
            }

            if (!returnedTagIds.add(tag.getTagId())) {
                throw new IllegalStateException(
                        "동일 리뷰에 tagId가 중복되었습니다: "
                                + tag.getTagId()
                );
            }

            if (!ALLOWED_SENTIMENTS.contains(
                    tag.getSentiment()
            )) {
                throw new IllegalStateException(
                        "허용되지 않은 sentiment입니다: "
                                + tag.getSentiment()
                );
            }
        }
    }

    private Map<Long, ReviewAnalysisTarget> createReviewMap(
            List<ReviewAnalysisTarget> reviews
    ) {
        Map<Long, ReviewAnalysisTarget> result =
                new HashMap<>();

        for (ReviewAnalysisTarget review : reviews) {
            result.put(review.getReviewId(), review);
        }

        return result;
    }

    private Map<Long, Set<Long>> createAllowedTagMap(
            List<PlaceTagCandidate> candidates
    ) {
        Map<Long, Set<Long>> result =
                new HashMap<>();

        for (PlaceTagCandidate candidate : candidates) {
            result.computeIfAbsent(
                    candidate.getPlaceId(),
                    ignored -> new HashSet<>()
            ).add(candidate.getTagId());
        }

        return result;
    }
}