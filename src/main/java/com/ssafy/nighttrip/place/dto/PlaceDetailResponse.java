package com.ssafy.nighttrip.place.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PlaceDetailResponse {


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
    private List<String> tags;

    public static PlaceDetailResponse from(PlaceDetailRow row) {
        return new PlaceDetailResponse(
                row.getPlaceId(),
                row.getCityId(),
                row.getCityName(),
                row.getName(),
                row.getCategory(),
                row.getAddress(),
                row.getRoadAddress(),
                row.getLatitude(),
                row.getLongitude(),
                row.getPhone(),
                row.getDescription(),
                row.getImageUrl(),
                row.getOpeningTime(),
                row.getClosingTime(),
                row.getLikeCount(),
                row.getReviewCount(),
                parseTags(row.getTagsCsv())
        );
    }

    private static List<String> parseTags(String tagsCsv) {
        if (tagsCsv == null || tagsCsv.isBlank()) {
            return Collections.emptyList();
        }

        return Arrays.stream(tagsCsv.split(","))
                .map(String::trim)
                .filter(tag -> !tag.isBlank())
                .toList();
    }
}
