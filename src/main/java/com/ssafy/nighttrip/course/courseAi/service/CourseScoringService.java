package com.ssafy.nighttrip.course.courseAi.service;

import com.ssafy.nighttrip.course.courseAi.domain.PlaceCandidate;
import com.ssafy.nighttrip.course.courseAi.domain.RouteCandidate;
import com.ssafy.nighttrip.course.courseAi.domain.RoutePlace;
import com.ssafy.nighttrip.course.courseAi.dto.CourseAnalyzeSlot;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CourseScoringService {

    private static final int BEAM_WIDTH = 30;
    private static final double MAX_SEGMENT_DISTANCE_KM = 20.0;

    private static final double TAG_WEIGHT = 0.7;
    private static final double DISTANCE_WEIGHT = 0.3;
    private static final double BAN_TAG_PENALTY_WEIGHT = 0.6;

    public List<RouteCandidate> generateRoutes(
            List<CourseAnalyzeSlot> slots,
            List<List<PlaceCandidate>> candidatesBySlot
    ) {
        List<RouteCandidate> routes = new ArrayList<>();
        routes.add(new RouteCandidate());

        for (int slotIndex = 0; slotIndex < slots.size(); slotIndex++) {
            CourseAnalyzeSlot slot = slots.get(slotIndex);
            List<PlaceCandidate> candidates = candidatesBySlot.get(slotIndex);

            List<RouteCandidate> expandedRoutes = new ArrayList<>();

            for (RouteCandidate route : routes) {
                for (PlaceCandidate candidate : candidates) {
                    if (route.containsPlace(candidate.getPlaceId())) {
                        continue;
                    }

                    RoutePlace routePlace = createRoutePlace(
                            route,
                            candidate,
                            slot,
                            slotIndex
                    );

                    if (routePlace == null) {
                        continue;
                    }

                    expandedRoutes.add(route.addPlace(routePlace));
                }
            }

            routes = expandedRoutes.stream()
                    .sorted(Comparator.comparingDouble(RouteCandidate::getTotalScore).reversed())
                    .limit(BEAM_WIDTH)
                    .collect(Collectors.toList());

            if (routes.isEmpty()) {
                return Collections.emptyList();
            }
        }

        return routes.stream()
                .sorted(Comparator.comparingDouble(RouteCandidate::getTotalScore).reversed())
                .collect(Collectors.toList());
    }

    private RoutePlace createRoutePlace(
            RouteCandidate route,
            PlaceCandidate candidate,
            CourseAnalyzeSlot slot,
            int slotIndex
    ) {
        double tagScore = calculateTagScore(candidate, slot);

        double distanceFromPreviousKm = 0.0;
        double distanceScore = 0.0;
        double placeScore;

        RoutePlace lastPlace = route.getLastPlace();

        if (lastPlace == null) {
            placeScore = tagScore;
        } else {
            distanceFromPreviousKm = calculateDistanceKm(
                    lastPlace.getLatitude(),
                    lastPlace.getLongitude(),
                    candidate.getLatitude(),
                    candidate.getLongitude()
            );

            if (distanceFromPreviousKm > MAX_SEGMENT_DISTANCE_KM) {
                return null;
            }

            distanceScore = calculateDistanceScore(distanceFromPreviousKm);
            placeScore = tagScore * TAG_WEIGHT + distanceScore * DISTANCE_WEIGHT;
        }
        return new RoutePlace(
                slotIndex + 1,
                candidate.getPlaceId(),
                candidate.getName(),
                candidate.getCategory(),
                candidate.getLatitude(),
                candidate.getLongitude(),
                round(placeScore * 100),
                round(tagScore * 100),
                round(distanceScore * 100),
                round(distanceFromPreviousKm),
                parseMatchedTags(candidate.getMatchedTags())
        );
    }

    private double calculateTagScore(PlaceCandidate candidate, CourseAnalyzeSlot slot) {
        int tagCount = slot.getTags() == null ? 0 : slot.getTags().size();
        int banTagCount = slot.getBanTags() == null ? 0 : slot.getBanTags().size();

        double preferredScore;

        if (tagCount == 0) {
            preferredScore = 0.5;
        } else {
            preferredScore = candidate.getPreferredTagScore() / tagCount;
        }

        double banPenalty = 0.0;

        if (banTagCount > 0) {
            banPenalty = candidate.getBanTagScore() / banTagCount;
        }

        double finalScore = preferredScore - banPenalty * BAN_TAG_PENALTY_WEIGHT;

        return clamp(finalScore, 0.0, 1.0);
    }

    private double calculateDistanceScore(double distanceKm) {
        double score = 1.0 - (distanceKm / MAX_SEGMENT_DISTANCE_KM);
        return clamp(score, 0.0, 1.0);
    }

    private double calculateDistanceKm(
            double lat1,
            double lon1,
            double lat2,
            double lon2
    ) {
        final int earthRadiusKm = 6371;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return earthRadiusKm * c;
    }

    private List<String> parseMatchedTags(String matchedTags) {
        if (matchedTags == null || matchedTags.isBlank()) {
            return List.of();
        }

        return Arrays.stream(matchedTags.split(","))
                .map(String::trim)
                .filter(tag -> !tag.isBlank())
                .toList();
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}