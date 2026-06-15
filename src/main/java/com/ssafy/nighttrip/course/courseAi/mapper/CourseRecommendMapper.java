package com.ssafy.nighttrip.course.courseAi.mapper;

import com.ssafy.nighttrip.course.courseAi.domain.PlaceCandidate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CourseRecommendMapper {

    List<PlaceCandidate> findCandidates(
            @Param("city") String city,
            @Param("category") String category,
            @Param("tags") List<String> tags,
            @Param("banTags") List<String> banTags
//            @Param("limit") int limit
    );
}