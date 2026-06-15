package com.ssafy.nighttrip.course.courseAi.mobility;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kakao-mobility")
public record KakaoMobilityProperties(
        String baseUrl,
        String restApiKey
) {
}