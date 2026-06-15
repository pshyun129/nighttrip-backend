package com.ssafy.nighttrip.review.analysis.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "review-analysis")
public record ReviewAnalysisProperties(
        boolean enabled,
        int batchSize,
        int priorWeight,
        String zone
) {
}