package com.ssafy.nighttrip.city.controller;

import com.ssafy.nighttrip.city.domain.City;
import com.ssafy.nighttrip.city.dto.CityResponse;
import com.ssafy.nighttrip.city.service.CityService;
import com.ssafy.nighttrip.global.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cities")
public class CityController {

    private final CityService cityService;


    // 전체 도시 정보 불러오기
    @GetMapping
    public ResponseEntity<ApiResponse<List<CityResponse>>> getAllCities(
            Authentication authentication,
            HttpServletRequest request
    ) {
        List<CityResponse> cities = cityService.findAll();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(
                   HttpStatus.OK,
                   "도시 전체 조회 성공",
                   cities,
                   request
                ));

    }

    // 세부 도시 정보 불러오기
    @GetMapping("/{cityId}")
    public ResponseEntity<ApiResponse<CityResponse>> getCityById(
            @PathVariable Long cityId,
            Authentication authentication,
            HttpServletRequest request
    ){
        CityResponse response = cityService.findById(cityId);

        return ResponseEntity
                .status(200)
                .body(ApiResponse.success(
                        HttpStatus.OK,
                        "도시 상세정보 조회 성공",
                        response,
                        request
                ));

    }

}
