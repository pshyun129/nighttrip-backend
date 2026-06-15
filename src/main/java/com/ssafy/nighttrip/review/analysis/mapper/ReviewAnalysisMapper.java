package com.ssafy.nighttrip.review.analysis.mapper;

import com.ssafy.nighttrip.review.analysis.domain.PlaceTagCandidate;
import com.ssafy.nighttrip.review.analysis.domain.PlaceTagScoreDelta;
import com.ssafy.nighttrip.review.analysis.domain.ReviewAnalysisTarget;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ReviewAnalysisMapper {

    List<ReviewAnalysisTarget> findReviewsCreatedBetween(
            @Param("startAt") LocalDateTime startAt,
            @Param("endAt") LocalDateTime endAt
    );

    List<PlaceTagCandidate> findPlaceTagCandidates(
            @Param("placeIds") List<Long> placeIds
    );

    int addSentimentCounts(PlaceTagScoreDelta delta);

    int recalculateConfidence(
            @Param("placeId") Long placeId,
            @Param("tagId") Long tagId,
            @Param("priorWeight") int priorWeight
    );
}