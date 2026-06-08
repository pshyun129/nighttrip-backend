package com.ssafy.nighttrip.review.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Review {

    private Long reviewId;
    private Long userId;
    private Long placeId;
    private String content;
    private LocalDate visitDate;
    private String visibility;
    private String status;
    private Integer likeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}