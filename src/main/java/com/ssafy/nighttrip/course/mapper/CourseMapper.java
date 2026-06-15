package com.ssafy.nighttrip.course.mapper;

import com.ssafy.nighttrip.course.domain.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CourseMapper {

    void insertCourse(Course course);

    void insertCoursePlace(
            @Param("courseId") Long courseId,
            @Param("placeId") Long placeId,
            @Param("sequence") Integer sequence,
            @Param("travelMinutesFromPrevious") Integer travelMinutesFromPrevious
    );
}