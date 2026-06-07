package com.ssafy.nighttrip.city.domain;


import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class City {

    private Long cityId;
    private String name;
    private String region;
    private String description;
    private String imageUrl;

}