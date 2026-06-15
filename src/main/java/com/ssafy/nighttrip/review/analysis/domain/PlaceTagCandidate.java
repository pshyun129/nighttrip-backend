package com.ssafy.nighttrip.review.analysis.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PlaceTagCandidate {

    private Long placeId;
    private Long tagId;
    private String tagName;
}