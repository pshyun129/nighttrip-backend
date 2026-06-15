package com.ssafy.nighttrip.course.courseAi.mobility.dto;

import java.util.List;

public record KakaoDirectionsResponse(
        List<Route> routes
) {
    public record Route(
            int result_code,
            String result_msg,
            Summary summary
    ) {
    }

    public record Summary(
            int distance,
            int duration
    ) {
    }
}