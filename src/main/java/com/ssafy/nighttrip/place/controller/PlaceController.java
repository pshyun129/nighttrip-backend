package com.ssafy.nighttrip.place.controller;

import com.ssafy.nighttrip.global.response.ApiResponse;
import com.ssafy.nighttrip.global.response.PageResponse;
import com.ssafy.nighttrip.place.dto.PlaceDetailResponse;
import com.ssafy.nighttrip.place.dto.PlaceListResponse;
import com.ssafy.nighttrip.place.dto.PlaceSearchRequest;
import com.ssafy.nighttrip.place.service.PlaceService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/place")
public class PlaceController {

    private final PlaceService placeService;

    // 장소 검색
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<PlaceListResponse>>> findPlaces(
            @ParameterObject PlaceSearchRequest placeSearchRequest,
            HttpServletRequest request
    ) {
        PageResponse<PlaceListResponse> response = placeService.findPlaces(placeSearchRequest);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(
                        HttpStatus.OK,
                        "장소 목록 조회가 완료되었습니다.",
                        response,
                        request
                ));
    }

    // 장소 상세 정보 조회
    @GetMapping("/{placeId}")
    public ResponseEntity<ApiResponse<PlaceDetailResponse>> findPlaceDetail(
            @PathVariable Long placeId,
            HttpServletRequest request
    ){
        PlaceDetailResponse response = placeService.findPlaceDetail(placeId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(
                        HttpStatus.OK,
                        "상세 장소 정보 조회 성공",
                        response,
                        request
                ));

    }

    // 장소 즐겨찾기 추가
    @PostMapping("/{placeId}/favorite")
    public ResponseEntity<ApiResponse<Void>> addFavorite(
        @PathVariable Long placeId,
        Authentication authentication,
        HttpServletRequest request
    ){
        Long userId = (Long) authentication.getPrincipal();

        placeService.addFavoritePlace(userId, placeId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(
                        HttpStatus.OK,
                        "즐겨찾기 등록 성공",
                        request
                ));

    }

    // 장소 즐겨찾기 제거
    @DeleteMapping("/{placeId}/favorite")
    public ResponseEntity<ApiResponse<Void>> removeFavorite(
            @PathVariable Long placeId,
            Authentication authentication,
            HttpServletRequest request
    ){
        Long userId = (Long) authentication.getPrincipal();
        placeService.removeFavoritePlace(userId, placeId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(
                        HttpStatus.OK,
                        "즐겨찾기 삭제 성공",
                        request
                ));
    }


}