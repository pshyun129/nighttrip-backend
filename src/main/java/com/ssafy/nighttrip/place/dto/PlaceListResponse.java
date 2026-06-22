package com.ssafy.nighttrip.place.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
@AllArgsConstructor
public class PlaceListResponse {

    private Long placeId;
    private String name;
    private String category;
    private String imageUrl;
    private String summary;

    // 위도 경도 추가
    private BigDecimal latitude;
    private BigDecimal longitude;

    private Long likeCount;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isFavorite;

    private List<String> tags;

    public static PlaceListResponse from(PlaceListRow row) {
        return new PlaceListResponse(
                row.getPlaceId(),
                row.getName(),
                row.getCategory(),
                row.getImageUrl(),
                row.getSummary(),
                row.getLatitude(),
                row.getLongitude(),
                row.getLikeCount(),
                row.getIsFavorite(),
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