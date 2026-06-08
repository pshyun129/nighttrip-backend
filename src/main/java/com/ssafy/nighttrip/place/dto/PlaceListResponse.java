package com.ssafy.nighttrip.place.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

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
    private Long likeCount;
    private List<String> tags;

    public static PlaceListResponse from(PlaceListRow row) {
        return new PlaceListResponse(
                row.getPlaceId(),
                row.getName(),
                row.getCategory(),
                row.getImageUrl(),
                row.getSummary(),
                row.getLikeCount(),
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