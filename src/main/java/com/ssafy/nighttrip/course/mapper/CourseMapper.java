package com.ssafy.nighttrip.course.mapper;

import com.ssafy.nighttrip.course.domain.Course;
import com.ssafy.nighttrip.course.dto.MyCourseDetailResponse;
import com.ssafy.nighttrip.course.dto.MyCourseListResponse;
import com.ssafy.nighttrip.course.dto.MyCoursePlaceResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalTime;
import java.util.List;

@Mapper
public interface CourseMapper {

    void insertCourse(Course course);

    void insertCoursePlace(
            @Param("courseId") Long courseId,
            @Param("placeId") Long placeId,
            @Param("sequence") Integer sequence,
            @Param("travelMinutesFromPrevious") Integer travelMinutesFromPrevious
    );

    Course findCourseByIdAndUserId(
            @Param("courseId") Long courseId,
            @Param("userId") Long userId
    );

    int updateCourse(
            @Param("courseId") Long courseId,
            @Param("userId") Long userId,
            @Param("cityId") Long cityId,
            @Param("title") String title,
            @Param("description") String description,
            @Param("theme") String theme,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("totalDurationMinutes") Integer totalDurationMinutes,
            @Param("totalTravelMinutes") Integer totalTravelMinutes,
            @Param("transport") String transport
    );

    void deleteCoursePlaces(@Param("courseId") Long courseId);




    List<MyCourseListResponse> findMyCourses(
            @Param("userId") Long userId,
            @Param("size") int size,
            @Param("offset") int offset
    );

    long countMyCourses(@Param("userId") Long userId);

    MyCourseDetailResponse findMyCourseDetail(
            @Param("userId") Long userId,
            @Param("courseId") Long courseId
    );

    List<MyCoursePlaceResponse> findMyCoursePlaces(@Param("courseId") Long courseId);

}