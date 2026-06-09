package com.ssafy.nighttrip.review.controller;

import com.ssafy.nighttrip.global.response.ApiResponse;
import com.ssafy.nighttrip.global.response.PageResponse;
import com.ssafy.nighttrip.review.dto.*;
import com.ssafy.nighttrip.review.service.ReviewService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/review")
public class ReviewController {

    private final ReviewService reviewService;

    // 장소별 리뷰 조회
    @GetMapping("/place/{placeId}")
    public ResponseEntity<ApiResponse<PageResponse<PlaceReviewListResponse>>> findReviewsByPlaceId(
            @PathVariable Long placeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request
    ) {
        PageResponse<PlaceReviewListResponse> response = reviewService.findReviewsByPlaceId(
                placeId,
                page,
                size
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(
                        HttpStatus.OK,
                        "장소별 리뷰 목록 조회가 완료되었습니다.",
                        response,
                        request
                ));
    }

    // 전체 리뷰 조회
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<AllReviewListResponse>>> findAllReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request
    ) {
        PageResponse<AllReviewListResponse> response = reviewService.findAllReviews(page, size);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(
                        HttpStatus.OK,
                        "전체 리뷰 목록 조회가 완료되었습니다.",
                        response,
                        request
                ));
    }

    // 리뷰 작성
    @PostMapping("/{placeId}")
    public ResponseEntity<ApiResponse<CreateReviewResponse>> createReview(
            @PathVariable Long placeId,
            Authentication authentication,
            @Valid @RequestBody CreateReviewRequest createReviewRequest,
            HttpServletRequest request
    ) {
        Long userId = (Long) authentication.getPrincipal();

        CreateReviewResponse response = reviewService.createReview(
                userId,
                placeId,
                createReviewRequest
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        HttpStatus.CREATED,
                        "리뷰가 작성되었습니다.",
                        response,
                        request
                ));
    }



    // 리뷰 수정
    @PatchMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewDetailResponse>> updateReview(
            @PathVariable Long reviewId,
            Authentication authentication,
            @Valid @RequestBody UpdateReviewRequest updateReviewRequest,
            HttpServletRequest request
    ) {
        Long userId = (Long) authentication.getPrincipal();

        ReviewDetailResponse response = reviewService.updateReview(
                userId,
                reviewId,
                updateReviewRequest
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(
                        HttpStatus.OK,
                        "리뷰가 수정되었습니다.",
                        response,
                        request
                ));
    }

    // 리뷰 삭제
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @PathVariable Long reviewId,
            Authentication authentication,
            HttpServletRequest request
    ){
        Long userId = (Long) authentication.getPrincipal();

        reviewService.deleteReview(userId, reviewId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(
                        HttpStatus.OK,
                        "리뷰 삭제 완료",
                        request
                ));
    }
    // 리뷰 좋아요 추가
    @PostMapping("/{reviewId}/like")
    public ResponseEntity<ApiResponse<Void>> addReviewLike(
            @PathVariable Long reviewId,
            Authentication authentication,
            HttpServletRequest request
    ) {
        Long userId = (Long) authentication.getPrincipal();

        reviewService.addReviewLike(userId, reviewId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(
                        HttpStatus.OK,
                        "리뷰 좋아요가 추가되었습니다.",
                        request
                ));
    }
    // 리뷰 좋아요 제거
    @DeleteMapping("/{reviewId}/like")
    public ResponseEntity<ApiResponse<Void>> removeReviewLike(
            @PathVariable Long reviewId,
            Authentication authentication,
            HttpServletRequest request
    ) {
        Long userId = (Long) authentication.getPrincipal();

        reviewService.removeReviewLike(userId, reviewId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(
                        HttpStatus.OK,
                        "리뷰 좋아요가 취소되었습니다.",
                        request
                ));
    }




    // 리뷰 상세 조회, 일단 사용 안할 예정
    @GetMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewDetailResponse>> findReviewDetail(
            @PathVariable Long reviewId,
            HttpServletRequest request
    ) {
        ReviewDetailResponse response = reviewService.findReviewDetail(reviewId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(
                        HttpStatus.OK,
                        "리뷰 상세 조회가 완료되었습니다.",
                        response,
                        request
                ));
    }
}