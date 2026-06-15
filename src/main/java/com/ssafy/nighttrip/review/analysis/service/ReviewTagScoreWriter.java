package com.ssafy.nighttrip.review.analysis.service;

import com.ssafy.nighttrip.review.analysis.config.ReviewAnalysisProperties;
import com.ssafy.nighttrip.review.analysis.domain.PlaceTagScoreDelta;
import com.ssafy.nighttrip.review.analysis.mapper.ReviewAnalysisMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class ReviewTagScoreWriter {

    private final ReviewAnalysisMapper reviewAnalysisMapper;
    private final ReviewAnalysisProperties properties;

    @Transactional
    public void apply(
            Collection<PlaceTagScoreDelta> deltas
    ) {
        for (PlaceTagScoreDelta delta : deltas) {
            int countUpdated =
                    reviewAnalysisMapper.addSentimentCounts(delta);

            if (countUpdated != 1) {
                throw new IllegalStateException(
                        "긍정·부정 개수 반영에 실패했습니다. placeId="
                                + delta.getPlaceId()
                                + ", tagId="
                                + delta.getTagId()
                );
            }

            int confidenceUpdated =
                    reviewAnalysisMapper.recalculateConfidence(
                            delta.getPlaceId(),
                            delta.getTagId(),
                            properties.priorWeight()
                    );

            if (confidenceUpdated != 1) {
                throw new IllegalStateException(
                        "confidence 계산에 실패했습니다. placeId="
                                + delta.getPlaceId()
                                + ", tagId="
                                + delta.getTagId()
                );
            }
        }
    }
}