package com.ssafy.nighttrip.place.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PlaceListRow {

    private Long placeId;
    private String name;
    private String category;
    private String imageUrl;
    private String summary;
    private Long likeCount;
    private String tagsCsv;
}