package com.ssafy.nighttrip.review.analysis.service;

import com.ssafy.nighttrip.review.analysis.config.ReviewAnalysisProperties;
import com.ssafy.nighttrip.review.analysis.domain.PlaceTagCandidate;
import com.ssafy.nighttrip.review.analysis.domain.PlaceTagScoreDelta;
import com.ssafy.nighttrip.review.analysis.domain.ReviewAnalysisTarget;
import com.ssafy.nighttrip.review.analysis.dto.ReviewAnalysisAiResponse;
import com.ssafy.nighttrip.review.analysis.dto.ReviewAnalysisPrompt;
import com.ssafy.nighttrip.review.analysis.mapper.ReviewAnalysisMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewAnalysisService {

    private static final LocalTime ANALYSIS_TIME =
            LocalTime.of(3, 0);

    private final ReviewAnalysisMapper reviewAnalysisMapper;
    private final GmsReviewAnalyzer gmsReviewAnalyzer;
    private final ReviewAnalysisValidator validator;
    private final ReviewTagScoreWriter scoreWriter;
    private final ReviewAnalysisProperties properties;

    public void analyzeFor(LocalDate targetDate) {
        LocalDateTime endAt =
                targetDate.atTime(ANALYSIS_TIME);

        LocalDateTime startAt =
                endAt.minusDays(1);

        log.info(
                "리뷰 태그 분석 시작: startAt={}, endAt={}",
                startAt,
                endAt
        );

        List<ReviewAnalysisTarget> reviews =
                reviewAnalysisMapper.findReviewsCreatedBetween(
                        startAt,
                        endAt
                );

        if (reviews.isEmpty()) {
            log.info("분석 대상 신규 리뷰가 없습니다.");
            return;
        }

        List<Long> placeIds = reviews.stream()
                .map(ReviewAnalysisTarget::getPlaceId)
                .distinct()
                .toList();

        List<PlaceTagCandidate> candidates =
                reviewAnalysisMapper.findPlaceTagCandidates(
                        placeIds
                );

        Map<Long, List<PlaceTagCandidate>> tagsByPlace =
                groupTagsByPlace(candidates);

        Map<PlaceTagKey, PlaceTagScoreDelta> deltas =
                new HashMap<>();

        for (List<ReviewAnalysisTarget> batch
                : partition(reviews, properties.batchSize())) {

            ReviewAnalysisPrompt prompt =
                    createPrompt(batch, tagsByPlace);

            ReviewAnalysisAiResponse response =
                    gmsReviewAnalyzer.analyze(prompt);

            validator.validate(
                    batch,
                    candidates,
                    response
            );

            aggregate(response, deltas);
        }

        if (deltas.isEmpty()) {
            log.info(
                    "긍정 또는 부정으로 판단된 태그가 없습니다."
            );
            return;
        }

        scoreWriter.apply(deltas.values());

        log.info(
                "리뷰 태그 분석 완료: reviewCount={}, updatedTagCount={}",
                reviews.size(),
                deltas.size()
        );
    }

    private ReviewAnalysisPrompt createPrompt(
            List<ReviewAnalysisTarget> reviews,
            Map<Long, List<PlaceTagCandidate>> tagsByPlace
    ) {
        List<ReviewAnalysisPrompt.ReviewItem> items =
                reviews.stream()
                        .map(review -> {
                            List<ReviewAnalysisPrompt.TagItem>
                                    allowedTags =
                                    tagsByPlace.getOrDefault(
                                                    review.getPlaceId(),
                                                    List.of()
                                            )
                                            .stream()
                                            .map(tag ->
                                                    new ReviewAnalysisPrompt.TagItem(
                                                            tag.getTagId(),
                                                            tag.getTagName()
                                                    )
                                            )
                                            .toList();

                            return new ReviewAnalysisPrompt.ReviewItem(
                                    review.getReviewId(),
                                    review.getPlaceId(),
                                    review.getPlaceName(),
                                    review.getContent(),
                                    allowedTags
                            );
                        })
                        .toList();

        return new ReviewAnalysisPrompt(items);
    }

    private void aggregate(
            ReviewAnalysisAiResponse response,
            Map<PlaceTagKey, PlaceTagScoreDelta> deltas
    ) {
        for (ReviewAnalysisAiResponse.ReviewResult reviewResult
                : response.getResults()) {

            for (ReviewAnalysisAiResponse.TagResult tagResult
                    : reviewResult.getTags()) {

                PlaceTagKey key = new PlaceTagKey(
                        reviewResult.getPlaceId(),
                        tagResult.getTagId()
                );

                PlaceTagScoreDelta delta =
                        deltas.computeIfAbsent(
                                key,
                                ignored ->
                                        new PlaceTagScoreDelta(
                                                reviewResult.getPlaceId(),
                                                tagResult.getTagId()
                                        )
                        );

                if ("POSITIVE".equals(tagResult.getSentiment())) {
                    delta.increasePositive();
                } else if ("NEGATIVE".equals(
                        tagResult.getSentiment()
                )) {
                    delta.increaseNegative();
                }
            }
        }
    }

    private Map<Long, List<PlaceTagCandidate>> groupTagsByPlace(
            List<PlaceTagCandidate> candidates
    ) {
        Map<Long, List<PlaceTagCandidate>> result =
                new HashMap<>();

        for (PlaceTagCandidate candidate : candidates) {
            result.computeIfAbsent(
                    candidate.getPlaceId(),
                    ignored -> new ArrayList<>()
            ).add(candidate);
        }

        return result;
    }

    private <T> List<List<T>> partition(
            List<T> source,
            int batchSize
    ) {
        if (batchSize <= 0) {
            throw new IllegalArgumentException(
                    "batchSize는 1 이상이어야 합니다."
            );
        }

        List<List<T>> result = new ArrayList<>();

        for (int start = 0;
             start < source.size();
             start += batchSize) {

            int end = Math.min(
                    start + batchSize,
                    source.size()
            );

            result.add(source.subList(start, end));
        }

        return result;
    }

    private record PlaceTagKey(
            Long placeId,
            Long tagId
    ) {
    }
}