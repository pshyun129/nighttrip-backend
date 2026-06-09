package com.ssafy.nighttrip.review.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewImageResponse {

    private Long reviewImageId;
    private String imageUrl;
    private Integer sortOrder;
}