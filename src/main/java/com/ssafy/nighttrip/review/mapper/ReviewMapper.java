package com.ssafy.nighttrip.review.mapper;

import com.ssafy.nighttrip.review.domain.Review;
import com.ssafy.nighttrip.review.dto.AllReviewListResponse;
import com.ssafy.nighttrip.review.dto.PlaceReviewListResponse;
import com.ssafy.nighttrip.review.dto.ReviewDetailResponse;
import com.ssafy.nighttrip.review.dto.ReviewImageResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReviewMapper {

    void insertReview(Review review);

    void insertReviewImage(
            @Param("reviewId") Long reviewId,
            @Param("imageUrl") String imageUrl,
            @Param("sortOrder") int sortOrder
    );


    ReviewDetailResponse findReviewDetailById(@Param("reviewId") Long reviewId);

    List<ReviewImageResponse> findReviewImagesByReviewId(@Param("reviewId") Long reviewId);

    Review findById(@Param("reviewId") Long reviewId);

    int updateReview(Review review);

    void deleteReviewImages(@Param("reviewId") Long reviewId);

    List<PlaceReviewListResponse> findReviewsByPlaceId(
            @Param("placeId") Long placeId,
            @Param("size") int size,
            @Param("offset") int offset
    );

    long countReviewsByPlaceId(@Param("placeId") Long placeId);


    List<AllReviewListResponse> findAllReviews(
            @Param("size") int size,
            @Param("offset") int offset
    );

    long countAllReviews();

    int deleteReview(@Param("reviewId") Long reviewId);

    int existsReviewLike(
            @Param("userId") Long userId,
            @Param("reviewId") Long reviewId
    );

    int insertReviewLike(
            @Param("userId") Long userId,
            @Param("reviewId") Long reviewId
    );

    int deleteReviewLike(
            @Param("userId") Long userId,
            @Param("reviewId") Long reviewId
    );

    int increaseReviewLikeCount(@Param("reviewId") Long reviewId);

    int decreaseReviewLikeCount(@Param("reviewId") Long reviewId);
}