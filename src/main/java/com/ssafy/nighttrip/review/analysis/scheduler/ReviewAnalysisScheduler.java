package com.ssafy.nighttrip.review.analysis.scheduler;

import com.ssafy.nighttrip.review.analysis.config.ReviewAnalysisProperties;
import com.ssafy.nighttrip.review.analysis.service.ReviewAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        prefix = "review-analysis",
        name = "enabled",
        havingValue = "true"
)
public class ReviewAnalysisScheduler {

    private final ReviewAnalysisService reviewAnalysisService;
    private final ReviewAnalysisProperties properties;

    @Scheduled(
            cron = "${review-analysis.cron}",
            zone = "${review-analysis.zone}"
    )
    public void analyzeReviews() {
        try {
            ZoneId zoneId =
                    ZoneId.of(properties.zone());

            LocalDate targetDate =
                    LocalDate.now(zoneId);

            reviewAnalysisService.analyzeFor(targetDate);

        } catch (Exception e) {
            log.error(
                    "GMS 리뷰 기반 태그 confidence 갱신에 실패했습니다.",
                    e
            );
        }
    }
}