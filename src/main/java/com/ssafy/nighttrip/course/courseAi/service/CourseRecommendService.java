package com.ssafy.nighttrip.course.courseAi.service;

import com.ssafy.nighttrip.course.courseAi.domain.PlaceCandidate;
import com.ssafy.nighttrip.course.courseAi.domain.RouteCandidate;
import com.ssafy.nighttrip.course.courseAi.domain.RoutePlace;
import com.ssafy.nighttrip.course.courseAi.dto.*;
//import com.ssafy.nighttrip.courseAi.dto.*;
import com.ssafy.nighttrip.course.courseAi.mapper.CourseRecommendMapper;
import com.ssafy.nighttrip.course.courseAi.mobility.KakaoMobilityClient;
import com.ssafy.nighttrip.course.courseAi.mobility.dto.MobilityResult;
import com.ssafy.nighttrip.global.exception.BusinessException;
import com.ssafy.nighttrip.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseRecommendService {

    private static final int CANDIDATE_LIMIT_PER_SLOT = 30;
    private static final int RESPONSE_COURSE_COUNT = 3;

    private static final int MOBILITY_TARGET_COUNT = 10;

    private static final double INTERNAL_SCORE_WEIGHT = 0.75;
    private static final double MOBILITY_SCORE_WEIGHT = 0.25;
    private static final double MAX_MOVE_MINUTES = 90.0;

    // 카카오 모빌리티
    private final KakaoMobilityClient kakaoMobilityClient;

    private static final Set<String> ALLOWED_CATEGORIES = Set.of(
            "관광지", "식당", "숙소", "술집"
    );

    private static final Set<String> ALLOWED_TAGS = Set.of(
            "야경", "전망", "해수욕장", "해안", "공원", "체험", "실내", "실외",
            "문화공간", "시장", "야시장", "스파", "산책", "노을", "먹거리", "사찰",
            "주차", "식사", "한식", "양식", "일식", "한정식", "육류", "해산물",
            "생선회", "고기구이", "분식", "뷔페", "술집", "카페", "호텔", "모텔",
            "펜션", "리조트", "게스트하우스", "한옥", "캠핑", "글램핑", "풀빌라",
            "오션뷰", "시티뷰"
    );

    private final CourseAnalyzeService courseAnalyzeService;
    private final CourseScoringService courseScoringService;
    private final CourseRecommendMapper courseRecommendMapper;

    public RecommendCourseResponse recommend(RecommendCourseRequest request) {
        CourseAnalyzeResponse analyzeResponse = courseAnalyzeService.analyze(request);

        List<CourseAnalyzeSlot> slots = normalizeSlots(analyzeResponse);

        if (slots.isEmpty()) {
            throw new BusinessException(ErrorCode.COURSE_INVALID_ANALYSIS_RESULT);
        }

        List<List<PlaceCandidate>> candidatesBySlot = slots.stream()
                .map(slot -> findCandidates(request.getCity(), slot))
                .toList();

        List<RouteCandidate> routes = courseScoringService.generateRoutes(
                slots,
                candidatesBySlot
        );

        if (routes.isEmpty()) {
            throw new BusinessException(ErrorCode.COURSE_CANDIDATE_NOT_FOUND);
        }

//        List<RouteCandidate> topRoutes = routes.stream()
//                .sorted(Comparator.comparingDouble(RouteCandidate::getTotalScore).reversed())
//                .limit(RESPONSE_COURSE_COUNT)
//                .toList();

        // ver2
//        List<RouteCandidate> sortedRoutes = routes.stream()
//                .sorted(Comparator.comparingDouble(RouteCandidate::getTotalScore).reversed())
//                .toList();
//
//        List<RouteCandidate> topRoutes = selectDiverseRoutes(
//                sortedRoutes,
//                RESPONSE_COURSE_COUNT
//        );

        List<RouteCandidate> sortedRoutes = routes.stream()
                .sorted(Comparator.comparingDouble(RouteCandidate::getTotalScore).reversed())
                .toList();

        /*
         * 1. 내부 점수 기준으로 카카오모빌리티 검사 대상 10개 선정
         *    - 아직 카카오 호출 안 함
         *    - 태그 점수 + 직선거리 기반 빔서치 결과만 사용
         */
        List<RouteCandidate> mobilityTargets = selectDiverseRoutes(
                sortedRoutes,
                MOBILITY_TARGET_COUNT
        );

        /*
         * 2. 상위 10개에 대해서만 카카오모빌리티 호출
         *    - 실제 도로 이동거리
         *    - 실제 예상 이동시간
         */
        List<RouteCandidate> routesWithMobility = applyMobility(mobilityTargets);

        /*
         * 3. 내부 점수 + 실제 이동시간 점수로 최종 재정렬
         *    - 그중 최종 3개 선택
         */
        List<RouteCandidate> topRoutes = routesWithMobility.stream()
                .sorted(Comparator.comparingDouble(this::calculateFinalScore).reversed())
                .limit(RESPONSE_COURSE_COUNT)
                .toList();

        List<RecommendedCourseDto> recommendations = new ArrayList<>();

        for (int i = 0; i < topRoutes.size(); i++) {
            recommendations.add(toRecommendedCourseDto(i + 1, topRoutes.get(i)));
        }

        return new RecommendCourseResponse(
                request.getCity(),
                request.getDate(),
                recommendations
        );
    }

    private List<CourseAnalyzeSlot> normalizeSlots(CourseAnalyzeResponse analyzeResponse) {
        if (analyzeResponse.getData() == null) {
            return List.of();
        }

        return analyzeResponse.getData().stream()
                .filter(slot -> ALLOWED_CATEGORIES.contains(slot.getCategory()))
                .map(this::normalizeSlotTags)
                .sorted(Comparator.comparingInt(CourseAnalyzeSlot::getNumber))
                .toList();
    }

    private CourseAnalyzeSlot normalizeSlotTags(CourseAnalyzeSlot slot) {
        List<String> tags = slot.getTags() == null
                ? List.of()
                : slot.getTags().stream()
                .filter(ALLOWED_TAGS::contains)
                .distinct()
                .toList();

        List<String> banTags = slot.getBanTags() == null
                ? List.of()
                : slot.getBanTags().stream()
                .filter(ALLOWED_TAGS::contains)
                .distinct()
                .toList();

        return new NormalizedCourseAnalyzeSlot(
                slot.getNumber(),
                slot.getCategory(),
                tags,
                banTags
        );
    }

    private List<PlaceCandidate> findCandidates(String city, CourseAnalyzeSlot slot) {
        List<PlaceCandidate> candidates = courseRecommendMapper.findCandidates(
                city,
                slot.getCategory(),
                slot.getTags(),
                slot.getBanTags()
//                CANDIDATE_LIMIT_PER_SLOT
        );

        if (candidates == null || candidates.isEmpty()) {
            throw new BusinessException(ErrorCode.COURSE_CANDIDATE_NOT_FOUND);
        }

        return candidates;
    }

    private RecommendedCourseDto toRecommendedCourseDto(int rank, RouteCandidate route) {
        List<RecommendedPlaceDto> places = route.getPlaces().stream()
                .map(this::toRecommendedPlaceDto)
                .toList();

        return new RecommendedCourseDto(
                rank,
                round(calculateFinalScore(route)),
                round(route.getTotalDistanceKm()),
                route.getEstimatedMoveMinutes(),
                places
        );
    }

    private RecommendedPlaceDto toRecommendedPlaceDto(RoutePlace place) {
        return new RecommendedPlaceDto(
                place.getOrder(),
                place.getPlaceId(),
                place.getName(),
                place.getCategory(),
                place.getLatitude(),
                place.getLongitude(),
                place.getScore(),
                place.getTagScore(),
                place.getDistanceScore(),
                place.getTags()
        );
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private static class NormalizedCourseAnalyzeSlot extends CourseAnalyzeSlot {

        private final int number;
        private final String category;
        private final List<String> tags;
        private final List<String> banTags;

        private NormalizedCourseAnalyzeSlot(
                int number,
                String category,
                List<String> tags,
                List<String> banTags
        ) {
            this.number = number;
            this.category = category;
            this.tags = tags;
            this.banTags = banTags;
        }

        @Override
        public int getNumber() {
            return number;
        }

        @Override
        public String getCategory() {
            return category;
        }

        @Override
        public List<String> getTags() {
            return tags;
        }

        @Override
        public List<String> getBanTags() {
            return banTags;
        }
    }

    private List<RouteCandidate> selectDiverseRoutes(
            List<RouteCandidate> sortedRoutes,
            int limit
    ) {
        List<RouteCandidate> selected = new ArrayList<>();

        for (RouteCandidate candidate : sortedRoutes) {
            boolean tooSimilar = selected.stream()
                    .anyMatch(selectedRoute -> countSamePlaces(selectedRoute, candidate) >= 2);

            if (tooSimilar) {
                continue;
            }

            selected.add(candidate);

            if (selected.size() == limit) {
                return selected;
            }
        }

        for (RouteCandidate candidate : sortedRoutes) {
            if (selected.size() == limit) {
                break;
            }

            if (!selected.contains(candidate)) {
                selected.add(candidate);
            }
        }

        return selected;
    }

    private long countSamePlaces(RouteCandidate route1, RouteCandidate route2) {
        Set<Long> placeIds = route1.getPlaces().stream()
                .map(RoutePlace::getPlaceId)
                .collect(Collectors.toSet());

        return route2.getPlaces().stream()
                .map(RoutePlace::getPlaceId)
                .filter(placeIds::contains)
                .count();
    }

//    private List<RouteCandidate> selectRoutesWithRestaurantDiversity(
//            List<RouteCandidate> sortedRoutes,
//            int limit
//    ) {
//        List<RouteCandidate> selected = new ArrayList<>();
//        Set<Long> usedRestaurantPlaceIds = new HashSet<>();
//
//        /*
//         * 1차 선택:
//         * - 식당 중복 금지
//         * - 코스가 너무 비슷하면 제외
//         */
//        for (RouteCandidate candidate : sortedRoutes) {
//            if (hasUsedRestaurant(candidate, usedRestaurantPlaceIds)) {
//                continue;
//            }
//
//            boolean tooSimilar = selected.stream()
//                    .anyMatch(selectedRoute -> countSamePlaces(selectedRoute, candidate) >= 2);
//
//            if (tooSimilar) {
//                continue;
//            }
//
//            selected.add(candidate);
//            usedRestaurantPlaceIds.addAll(getRestaurantPlaceIds(candidate));
//
//            if (selected.size() == limit) {
//                return selected;
//            }
//        }
//
//        /*
//         * 2차 선택:
//         * - 코스 유사도 조건은 완화
//         * - 식당 중복 금지는 유지
//         */
//        for (RouteCandidate candidate : sortedRoutes) {
//            if (selected.size() == limit) {
//                break;
//            }
//
//            if (selected.contains(candidate)) {
//                continue;
//            }
//
//            if (hasUsedRestaurant(candidate, usedRestaurantPlaceIds)) {
//                continue;
//            }
//
//            selected.add(candidate);
//            usedRestaurantPlaceIds.addAll(getRestaurantPlaceIds(candidate));
//        }
//
//        return selected;
//    }
//
//    private boolean hasUsedRestaurant(
//            RouteCandidate candidate,
//            Set<Long> usedRestaurantPlaceIds
//    ) {
//        return candidate.getPlaces().stream()
//                .filter(place -> "식당".equals(place.getCategory()))
//                .map(RoutePlace::getPlaceId)
//                .anyMatch(usedRestaurantPlaceIds::contains);
//    }
//
//    private Set<Long> getRestaurantPlaceIds(RouteCandidate route) {
//        return route.getPlaces().stream()
//                .filter(place -> "식당".equals(place.getCategory()))
//                .map(RoutePlace::getPlaceId)
//                .collect(Collectors.toSet());
//    }
//
//    private long countSamePlaces(
//            RouteCandidate route1,
//            RouteCandidate route2
//    ) {
//        Set<Long> placeIds = route1.getPlaces().stream()
//                .map(RoutePlace::getPlaceId)
//                .collect(Collectors.toSet());
//
//        return route2.getPlaces().stream()
//                .map(RoutePlace::getPlaceId)
//                .filter(placeIds::contains)
//                .count();
//    }


    private List<RouteCandidate> applyMobility(List<RouteCandidate> routes) {
        List<RouteCandidate> result = new ArrayList<>();

        for (RouteCandidate route : routes) {
            MobilityResult mobilityResult = kakaoMobilityClient.getRouteSummary(route);

            result.add(route.withMobility(
                    mobilityResult.distanceKm(),
                    mobilityResult.durationMinutes()
            ));
        }

        return result;
    }


    // 최종 카카오 모빌리티 시간 기반 점수 계산
    private double calculateFinalScore(RouteCandidate route) {
        double internalScore = route.getTotalScore();
        double mobilityScore = calculateMobilityScore(route.getEstimatedMoveMinutes());

        return internalScore * INTERNAL_SCORE_WEIGHT
                + mobilityScore * MOBILITY_SCORE_WEIGHT;
    }

    private double calculateMobilityScore(int estimatedMoveMinutes) {
        double score = 100.0 - ((estimatedMoveMinutes / MAX_MOVE_MINUTES) * 100.0);
        return clamp(score, 0.0, 100.0);
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

}