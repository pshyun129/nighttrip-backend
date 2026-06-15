package com.ssafy.nighttrip.course.courseAi.mobility;

import com.ssafy.nighttrip.course.courseAi.domain.RouteCandidate;
import com.ssafy.nighttrip.course.courseAi.domain.RoutePlace;
import com.ssafy.nighttrip.course.courseAi.mobility.dto.KakaoDirectionsResponse;
import com.ssafy.nighttrip.course.courseAi.mobility.dto.MobilityResult;
import com.ssafy.nighttrip.global.exception.BusinessException;
import com.ssafy.nighttrip.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;

@Component
@RequiredArgsConstructor
public class KakaoMobilityClient {

    private final RestClient kakaoMobilityRestClient;

    public MobilityResult getRouteSummary(RouteCandidate route) {
        List<RoutePlace> places = route.getPlaces();

        if (places.size() < 2) {
            return new MobilityResult(0, 0);
        }

        RoutePlace origin = places.get(0);
        RoutePlace destination = places.get(places.size() - 1);
        List<RoutePlace> waypoints = places.subList(1, places.size() - 1);

        try {
            KakaoDirectionsResponse response = kakaoMobilityRestClient
                    .get()
                    .uri(uriBuilder -> {
                        uriBuilder
                                .path("/v1/directions")
                                .queryParam("origin", toPoint(origin))
                                .queryParam("destination", toPoint(destination))
                                .queryParam("priority", "RECOMMEND")
                                .queryParam("summary", "true")
                                .queryParam("alternatives", "false")
                                .queryParam("road_details", "false");

                        if (!waypoints.isEmpty()) {
                            uriBuilder.queryParam(
                                    "waypoints",
                                    toWaypoints(waypoints)
                            );
                        }

                        return uriBuilder.build();
                    })
                    .retrieve()
                    .body(KakaoDirectionsResponse.class);

            return extractResult(response);

        } catch (RestClientResponseException e) {
            throw new BusinessException(ErrorCode.MOBILITY_API_FAILED);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.MOBILITY_API_FAILED);
        }
    }

    private MobilityResult extractResult(KakaoDirectionsResponse response) {
        if (response == null
                || response.routes() == null
                || response.routes().isEmpty()) {
            throw new BusinessException(ErrorCode.MOBILITY_API_FAILED);
        }

        KakaoDirectionsResponse.Route route = response.routes().get(0);

        if (route.result_code() != 0 || route.summary() == null) {
            throw new BusinessException(ErrorCode.MOBILITY_API_FAILED);
        }

        return new MobilityResult(
                route.summary().distance(),
                route.summary().duration()
        );
    }

    private String toPoint(RoutePlace place) {
        return place.getLongitude() + "," + place.getLatitude();
    }

    private String toWaypoints(List<RoutePlace> waypoints) {
        return waypoints.stream()
                .map(this::toPoint)
                .reduce((a, b) -> a + "|" + b)
                .orElse("");
    }
}