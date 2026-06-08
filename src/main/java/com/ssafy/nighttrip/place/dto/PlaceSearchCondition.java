package com.ssafy.nighttrip.place.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlaceSearchCondition {

    private String category;
    private String keyword;
    private int size;
    private int offset;
}