package com.ssafy.nighttrip.course.courseAi.domain;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class RouteCandidate {

    private final List<RoutePlace> places;
    private final double totalScore;
    private final double totalDistanceKm;
    private final int estimatedMoveMinutes;

    public RouteCandidate() {
        this.places = new ArrayList<>();
        this.totalScore = 0.0;
        this.totalDistanceKm = 0.0;
        this.estimatedMoveMinutes = 0;
    }

    private RouteCandidate(
            List<RoutePlace> places,
            double totalScore,
            double totalDistanceKm,
            int estimatedMoveMinutes
    ) {
        this.places = places;
        this.totalScore = totalScore;
        this.totalDistanceKm = totalDistanceKm;
        this.estimatedMoveMinutes = estimatedMoveMinutes;
    }

    public RouteCandidate addPlace(RoutePlace place) {
        List<RoutePlace> newPlaces = new ArrayList<>(this.places);
        newPlaces.add(place);

        double scoreSum = newPlaces.stream()
                .mapToDouble(RoutePlace::getScore)
                .sum();

        double averageScore = scoreSum / newPlaces.size();

        double newTotalDistanceKm = this.totalDistanceKm + place.getDistanceFromPreviousKm();

        int estimatedMinutes = (int) Math.ceil((newTotalDistanceKm / 25.0) * 60);

        return new RouteCandidate(
                newPlaces,
                averageScore,
                newTotalDistanceKm,
                estimatedMinutes
        );
    }

    public boolean containsPlace(Long placeId) {
        return places.stream()
                .anyMatch(place -> place.getPlaceId().equals(placeId));
    }

    public RoutePlace getLastPlace() {
        if (places.isEmpty()) {
            return null;
        }

        return places.get(places.size() - 1);
    }

    public RouteCandidate withMobility(
            double totalDistanceKm,
            int estimatedMoveMinutes
    ) {
        return new RouteCandidate(
                this.places,
                this.totalScore,
                totalDistanceKm,
                estimatedMoveMinutes
        );
    }
}