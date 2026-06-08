package com.ssafy.nighttrip.place.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class Place {

    private Long placeId;
    private Long cityId;
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
}