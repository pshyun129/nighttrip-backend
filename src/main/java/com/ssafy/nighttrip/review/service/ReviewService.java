package com.ssafy.nighttrip.review.service;

import com.ssafy.nighttrip.global.exception.BusinessException;
import com.ssafy.nighttrip.global.exception.ErrorCode;
import com.ssafy.nighttrip.global.response.PageResponse;
import com.ssafy.nighttrip.place.mapper.PlaceMapper;
import com.ssafy.nighttrip.review.domain.Review;
import com.ssafy.nighttrip.review.dto.*;
import com.ssafy.nighttrip.review.mapper.ReviewMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class  ReviewService {

    private final ReviewMapper reviewMapper;
    private final PlaceMapper placeMapper;


    // 리뷰 작성
    @Transactional
    public CreateReviewResponse createReview(
            Long userId,
            Long placeId,
            CreateReviewRequest request
    ) {
        if (placeMapper.existsById(placeId) == 0) {
            throw new BusinessException(ErrorCode.PLACE_NOT_FOUND);
        }

        Review review = new Review();
        review.setUserId(userId);
        review.setPlaceId(placeId);
        review.setContent(request.getContent());
        review.setVisitDate(request.getVisitDate());
        review.setVisibility("PUBLIC");

        reviewMapper.insertReview(review);

        saveReviewImages(review.getReviewId(), request.getImageUrls());

        return new CreateReviewResponse(review.getReviewId());
    }


    // 이미지 저장
    private void saveReviewImages(Long reviewId, List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return;
        }

        for (int i = 0; i < imageUrls.size(); i++) {
            String imageUrl = imageUrls.get(i);

            if (imageUrl == null || imageUrl.isBlank()) {
                continue;
            }

            reviewMapper.insertReviewImage(reviewId, imageUrl, i);
        }
    }

    // 리뷰 세부정보
    public ReviewDetailResponse findReviewDetail(Long reviewId) {
        ReviewDetailResponse response = reviewMapper.findReviewDetailById(reviewId);

        if (response == null) {
            throw new BusinessException(ErrorCode.REVIEW_NOT_FOUND);
        }

        response.setImages(reviewMapper.findReviewImagesByReviewId(reviewId));

        return response;
    }

    // 리뷰 업데이트
    @Transactional
    public ReviewDetailResponse updateReview(Long userId, Long reviewId, UpdateReviewRequest request) {
        Review review = reviewMapper.findById(reviewId);

        if (review == null) {
            throw new BusinessException(ErrorCode.REVIEW_NOT_FOUND);
        }

        if (!review.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.REVIEW_FORBIDDEN);
        }

        review.setContent(request.getContent());
        review.setVisitDate(request.getVisitDate());
        review.setVisibility("PUBLIC");

        int updatedCount = reviewMapper.updateReview(review);

        if (updatedCount == 0) {
            throw new BusinessException(ErrorCode.REVIEW_NOT_FOUND);
        }

        reviewMapper.deleteReviewImages(reviewId);
        saveReviewImages(reviewId, request.getImageUrls());

        return findReviewDetail(reviewId);
    }

    // 장소별 리뷰 목록
    public PageResponse<PlaceReviewListResponse> findReviewsByPlaceId(Long placeId, int page, int size) {
        validatePageRequest(page, size);

        if (placeMapper.existsById(placeId) == 0) {
            throw new BusinessException(ErrorCode.PLACE_NOT_FOUND);
        }

        int offset = page * size;

        long totalElements = reviewMapper.countReviewsByPlaceId(placeId);

        List<PlaceReviewListResponse> content = reviewMapper.findReviewsByPlaceId(
                placeId,
                size,
                offset
        );

        return PageResponse.of(content, page, size, totalElements);
    }

    // 페이지 범위 검사
    private void validatePageRequest(int page, int size) {
        if (page < 0) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        if (size < 1 || size > 50) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }

    // 전체 리뷰 조회
    public PageResponse<AllReviewListResponse> findAllReviews(int page, int size) {
        validatePageRequest(page, size);

        int offset = page * size;

        long totalElements = reviewMapper.countAllReviews();

        List<AllReviewListResponse> content = reviewMapper.findAllReviews(size, offset);

        return PageResponse.of(content, page, size, totalElements);
    }

    // 리뷰 삭제
    public void deleteReview(Long userId, Long reviewId) {
        Review review = reviewMapper.findById(reviewId);
        if (review == null) {
            throw new BusinessException(ErrorCode.REVIEW_NOT_FOUND);
        }
        if (!review.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.REVIEW_FORBIDDEN);
        }

        int deletedCount = reviewMapper.deleteReview(reviewId);

        if (deletedCount == 0) {
            throw new BusinessException(ErrorCode.REVIEW_NOT_FOUND);
        }
    }


    // 리뷰 좋아요 추가
    @Transactional
    public void addReviewLike(Long userId, Long reviewId) {
        Review review = reviewMapper.findById(reviewId);

        if (review == null) {
            throw new BusinessException(ErrorCode.REVIEW_NOT_FOUND);
        }

        if (reviewMapper.existsReviewLike(userId, reviewId) > 0) {
            throw new BusinessException(ErrorCode.REVIEW_ALREADY_LIKED);
        }

        reviewMapper.insertReviewLike(userId, reviewId);
        reviewMapper.increaseReviewLikeCount(reviewId);
    }

    // 리뷰 좋아요 제거
    @Transactional
    public void removeReviewLike(Long userId, Long reviewId) {
        Review review = reviewMapper.findById(reviewId);

        if (review == null) {
            throw new BusinessException(ErrorCode.REVIEW_NOT_FOUND);
        }

        int deletedCount = reviewMapper.deleteReviewLike(userId, reviewId);

        if (deletedCount == 0) {
            throw new BusinessException(ErrorCode.REVIEW_LIKE_NOT_FOUND);
        }

        reviewMapper.decreaseReviewLikeCount(reviewId);
    }

}