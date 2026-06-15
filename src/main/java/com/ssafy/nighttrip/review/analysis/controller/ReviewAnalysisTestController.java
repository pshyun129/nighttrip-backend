package com.ssafy.nighttrip.review.analysis.controller;

import com.ssafy.nighttrip.review.analysis.service.ReviewAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/review-analysis")
@ConditionalOnProperty(
        prefix = "review-analysis",
        name = "test-api-enabled",
        havingValue = "true"
)
public class ReviewAnalysisTestController {

    private final ReviewAnalysisService reviewAnalysisService;

    @PostMapping("/run")
    public ResponseEntity<Map<String, Object>> run(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate targetDate
    ) {
        reviewAnalysisService.analyzeFor(targetDate);

        return ResponseEntity.ok(
                Map.of(
                        "message", "리뷰 태그 분석이 완료되었습니다.",
                        "targetDate", targetDate,
                        "startAt", targetDate.minusDays(1).atTime(3, 0),
                        "endAt", targetDate.atTime(3, 0)
                )
        );
    }
}