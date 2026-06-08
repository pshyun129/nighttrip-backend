package com.ssafy.nighttrip.review.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ReviewImage {

    private Long reviewImageId;
    private Long reviewId;
    private String imageUrl;
    private Integer sortOrder;
    private LocalDateTime createdAt;
}