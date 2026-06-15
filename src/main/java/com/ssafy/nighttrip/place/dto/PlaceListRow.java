package com.ssafy.nighttrip.place.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class PlaceListRow {

    private Long placeId;
    private String name;
    private String category;
    private String imageUrl;
    private String summary;

    // 위도 경도 추가
    private BigDecimal latitude;
    private BigDecimal longitude;

    private Long likeCount;
    private String tagsCsv;
}