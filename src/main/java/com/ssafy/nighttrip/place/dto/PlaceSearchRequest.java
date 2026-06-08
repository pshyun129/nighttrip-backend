package com.ssafy.nighttrip.place.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.StringUtils;

@Getter
@Setter
@NoArgsConstructor
public class PlaceSearchRequest {

    private String category;
    private String keyword;
    private Integer page;
    private Integer size;

    public String categoryOrNull() {
        return StringUtils.hasText(category) ? category.trim() : null;
    }

    public String keywordOrNull() {
        return StringUtils.hasText(keyword) ? keyword.trim() : null;
    }

    public int pageOrDefault() {
        return page == null ? 0 : page;
    }

    public int sizeOrDefault() {
        return size == null ? 10 : size;
    }
}