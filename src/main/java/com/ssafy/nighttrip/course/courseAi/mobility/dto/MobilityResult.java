package com.ssafy.nighttrip.course.courseAi.mobility.dto;

public record MobilityResult(
        int distanceMeters,
        int durationSeconds
) {
    public double distanceKm() {
        return Math.round((distanceMeters / 1000.0) * 100.0) / 100.0;
    }

    public int durationMinutes() {
        return (int) Math.ceil(durationSeconds / 60.0);
    }
}