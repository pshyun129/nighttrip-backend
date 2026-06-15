package com.ssafy.nighttrip.course.courseAi.controller;

import com.ssafy.nighttrip.course.courseAi.dto.RecommendCourseRequest;
import com.ssafy.nighttrip.course.courseAi.dto.RecommendCourseResponse;
import com.ssafy.nighttrip.course.courseAi.service.CourseRecommendService;
import com.ssafy.nighttrip.global.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/course")
public class CourseRecommendController {

    private final CourseRecommendService courseRecommendService;

    @PostMapping("/recommend")
    public ResponseEntity<ApiResponse<RecommendCourseResponse>> recommendCourse(
            @Valid @RequestBody RecommendCourseRequest recommendCourseRequest,
            HttpServletRequest request
    ) {
        RecommendCourseResponse response = courseRecommendService.recommend(recommendCourseRequest);

//        return ApiResponse.success(
//                "코스 추천이 완료되었습니다.",
//                response
//        );
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                   HttpStatus.OK,
                   "코스 추천이 완료되었습니다",
                   response,
                   request
                ));

    }
}