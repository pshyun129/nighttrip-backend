package com.ssafy.nighttrip.review.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
@NoArgsConstructor
public class AllReviewListResponse {

    private Long reviewId;
    private String placeName;
    private String nickname;
    private String content;
    private Integer likeCount;
    private LocalDateTime createdAt;

    @JsonIgnore
    private String imageUrlsCsv;

    public List<String> getImageUrls() {
        if (imageUrlsCsv == null || imageUrlsCsv.isBlank()) {
            return Collections.emptyList();
        }

        return Arrays.stream(imageUrlsCsv.split(","))
                .map(String::trim)
                .filter(url -> !url.isBlank())
                .toList();
    }
}