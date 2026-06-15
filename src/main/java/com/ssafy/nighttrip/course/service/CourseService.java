package com.ssafy.nighttrip.course.service;

import com.ssafy.nighttrip.city.mapper.CityMapper;
import com.ssafy.nighttrip.course.domain.Course;
import com.ssafy.nighttrip.course.dto.SaveCourseItemRequest;
import com.ssafy.nighttrip.course.dto.SaveCoursePlaceRequest;
import com.ssafy.nighttrip.course.dto.SaveCourseRequest;
import com.ssafy.nighttrip.course.dto.SaveCourseResponse;
import com.ssafy.nighttrip.course.mapper.CourseMapper;
import com.ssafy.nighttrip.global.exception.BusinessException;
import com.ssafy.nighttrip.global.exception.ErrorCode;
import com.ssafy.nighttrip.place.mapper.PlaceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseMapper courseMapper;
    private final CityMapper cityMapper;
    private final PlaceMapper placeMapper;


    @Transactional
    public SaveCourseResponse saveCourses(Long userId, SaveCourseRequest request) {
        validateCourseCount(request.getCourses());

        Long cityId = cityMapper.findIdByName(request.getCity());

        if (cityId == null) {
            throw new BusinessException(ErrorCode.CITY_NOT_FOUND);
        }

        List<Long> savedCourseIds = new ArrayList<>();

        for (SaveCourseItemRequest courseRequest : request.getCourses()) {
            validateCoursePlaces(courseRequest.getPlaces());

            Course course = new Course();
            course.setCityId(cityId);
            course.setUserId(userId);
            course.setTitle(makeTitle(request, courseRequest));
            course.setDescription(makeDescription(request, courseRequest));
            course.setTheme("AI_RECOMMEND");

            course.setStartTime(null);
            course.setEndTime(null);

            course.setTotalTravelMinutes(courseRequest.getEstimatedMoveMinutes());
            course.setTotalDurationMinutes(courseRequest.getEstimatedMoveMinutes());

            course.setTransport("CAR");

            courseMapper.insertCourse(course);

            saveCoursePlaces(course.getCourseId(), courseRequest.getPlaces());

            savedCourseIds.add(course.getCourseId());
        }

        return new SaveCourseResponse(savedCourseIds);
    }

    private void validateCourseCount(List<SaveCourseItemRequest> courses) {
        if (courses == null || courses.isEmpty() || courses.size() > 3) {
            throw new BusinessException(ErrorCode.INVALID_COURSE_COUNT);
        }
    }

    private void validateCoursePlaces(List<SaveCoursePlaceRequest> places) {
        if (places == null || places.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_COURSE_PLACE_COUNT);
        }

        for (SaveCoursePlaceRequest place : places) {
            if (place.getPlaceId() == null || placeMapper.existsById(place.getPlaceId()) == 0) {
                throw new BusinessException(ErrorCode.PLACE_NOT_FOUND);
            }
        }
    }

    private void saveCoursePlaces(Long courseId, List<SaveCoursePlaceRequest> places) {
        List<SaveCoursePlaceRequest> sortedPlaces = places.stream()
                .sorted(Comparator.comparing(SaveCoursePlaceRequest::getOrder))
                .toList();

        for (SaveCoursePlaceRequest place : sortedPlaces) {
            courseMapper.insertCoursePlace(
                    courseId,
                    place.getPlaceId(),
                    place.getOrder(),
                    0
            );
        }
    }

    private String makeTitle(SaveCourseRequest request, SaveCourseItemRequest courseRequest) {
        if (courseRequest.getRank() == null) {
            return request.getCity() + " 추천 코스";
        }

        return request.getCity() + " 추천 코스 " + courseRequest.getRank();
    }

    private String makeDescription(SaveCourseRequest request, SaveCourseItemRequest courseRequest) {
        int placeCount = courseRequest.getPlaces() == null
                ? 0
                : courseRequest.getPlaces().size();

        if (request.getDate() == null) {
            return request.getCity() + " AI 추천 코스입니다. 총 " + placeCount + "개의 장소로 구성되어 있습니다.";
        }

        return request.getDate() + " " + request.getCity() + " AI 추천 코스입니다. 총 " + placeCount + "개의 장소로 구성되어 있습니다.";
    }
}