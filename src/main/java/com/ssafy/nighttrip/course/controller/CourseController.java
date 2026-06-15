package com.ssafy.nighttrip.course.controller;

import com.ssafy.nighttrip.course.dto.SaveCourseRequest;
import com.ssafy.nighttrip.course.dto.SaveCourseResponse;
import com.ssafy.nighttrip.course.service.CourseService;
import com.ssafy.nighttrip.global.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/course")
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    public ResponseEntity<ApiResponse<SaveCourseResponse>> saveCourses(
            Authentication authentication,
            @Valid @RequestBody SaveCourseRequest saveCourseRequest,
            HttpServletRequest request
    ) {
        Long userId = (Long) authentication.getPrincipal();

        SaveCourseResponse response = courseService.saveCourses(
                userId,
                saveCourseRequest
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        HttpStatus.CREATED,
                        "코스가 저장되었습니다.",
                        response,
                        request
                ));
    }
}