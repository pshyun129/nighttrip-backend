package com.ssafy.nighttrip.review.analysis.domain;

import lombok.Getter;

@Getter
public class PlaceTagScoreDelta {

    private final Long placeId;
    private final Long tagId;

    private long positiveDelta;
    private long negativeDelta;

    public PlaceTagScoreDelta(Long placeId, Long tagId) {
        this.placeId = placeId;
        this.tagId = tagId;
    }

    public void increasePositive() {
        positiveDelta++;
    }

    public void increaseNegative() {
        negativeDelta++;
    }
}