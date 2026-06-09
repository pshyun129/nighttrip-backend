package com.ssafy.nighttrip.review.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ReviewDetailResponse {

    private Long reviewId;
    private Long placeId;
    private String placeName;
    private Long userId;
    private String nickname;
    private String content;
    private LocalDate visitDate;
    private String visibility;
    private Integer likeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<ReviewImageResponse> images = new ArrayList<>();
}