package com.ssafy.nighttrip.place.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class PlaceDetailRow {

    private Long placeId;
    private Long cityId;
    private String cityName;
    private String name;
    private String category;
    private String address;
    private String roadAddress;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String phone;
    private String description;
    private String imageUrl;
    private String openingTime;
    private String closingTime;
    private Long likeCount;
    private Long reviewCount;
    private String tagsCsv;
}