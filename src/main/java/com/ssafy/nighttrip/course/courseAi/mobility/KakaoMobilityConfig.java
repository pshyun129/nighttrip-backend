package com.ssafy.nighttrip.course.courseAi.mobility;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(KakaoMobilityProperties.class)
public class KakaoMobilityConfig {

    @Bean
    public RestClient kakaoMobilityRestClient(
            RestClient.Builder builder,
            KakaoMobilityProperties properties
    ) {
        validate(properties);

        return builder
                .baseUrl(properties.baseUrl())
                .defaultHeader(
                        HttpHeaders.AUTHORIZATION,
                        "KakaoAK " + properties.restApiKey()
                )
                .build();
    }

    private void validate(KakaoMobilityProperties properties) {
        if (properties.baseUrl() == null || properties.baseUrl().isBlank()) {
            throw new IllegalStateException("카카오모빌리티 base-url이 설정되지 않았습니다.");
        }

        if (properties.restApiKey() == null || properties.restApiKey().isBlank()) {
            throw new IllegalStateException("카카오 REST API KEY가 설정되지 않았습니다.");
        }
    }
}