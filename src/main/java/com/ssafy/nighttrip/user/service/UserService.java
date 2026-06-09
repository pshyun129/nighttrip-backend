package com.ssafy.nighttrip.user.service;

import com.ssafy.nighttrip.auth.service.RefreshTokenService;
import com.ssafy.nighttrip.global.exception.BusinessException;
import com.ssafy.nighttrip.global.exception.ErrorCode;
import com.ssafy.nighttrip.global.response.PageResponse;
import com.ssafy.nighttrip.place.dto.PlaceListResponse;
import com.ssafy.nighttrip.place.mapper.PlaceMapper;

import com.ssafy.nighttrip.review.dto.MyReviewListResponse;
import com.ssafy.nighttrip.review.mapper.ReviewMapper;
import com.ssafy.nighttrip.user.domain.User;
import com.ssafy.nighttrip.user.dto.DeleteMyInfoRequest;
import com.ssafy.nighttrip.user.dto.MyInfoResponse;
import com.ssafy.nighttrip.user.dto.UpdateMyInfoRequest;
import com.ssafy.nighttrip.user.dto.UpdateMyPasswordRequest;
import com.ssafy.nighttrip.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final PlaceMapper placeMapper;
    private final ReviewMapper reviewMapper;


    public MyInfoResponse getMyInfo(Long userId) {

        User user = userMapper.findById(userId);

        // 만약 user가 존재하지 않는다면
        if(user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }


        MyInfoResponse myInfoResponse = new MyInfoResponse(
                user.getUserId(),
                user.getEmail(),
                user.getNickname(),
                user.getRole(),
                user.getProfileImageUrl(),
                user.getCreatedAt()
        );

        return myInfoResponse;
    }

    // 내 정보 업데이트
    public void updateMyInfo(Long userId, UpdateMyInfoRequest updateMyInfoRequest) {
        User user = userMapper.findById(userId);

        // 만약 user가 존재하지 않는다면
        if(user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        userMapper.updateMyInfo(userId, updateMyInfoRequest.getNickname());

    }

    // 회원 탈퇴
    @Transactional
    public void deleteMyInfo(Long userId, DeleteMyInfoRequest deleteMyInfoRequest) {
        User user = userMapper.findById(userId);
        if(user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 비밀번호 체크
        if (!passwordEncoder.matches(deleteMyInfoRequest.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_NOT_MATCHED);
        }

        refreshTokenService.deleteRefreshToken(userId);

        userMapper.deleteMyCourse(userId);

        int result = userMapper.deleteMyInfo(userId);

        if(result == 0) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

    }

    // 비밀번호 업데이트
    public void updatePassword(Long userId, UpdateMyPasswordRequest updateMyPasswordRequest) {
        User user = userMapper.findById(userId);
        if(user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 비밀번호 체크
        if (!passwordEncoder.matches(updateMyPasswordRequest.getCurrentPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_NOT_MATCHED);
        }

        String password = passwordEncoder.encode(updateMyPasswordRequest.getNewPassword());

        userMapper.updateMyPassword(userId, password);

    }

    // 내 장소 즐겨찾기 조회
    public PageResponse<PlaceListResponse> getMyFavorites(Long userId, int page, int size) {
        validatePageRequest(page, size);

        User user = userMapper.findById(userId);

        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        int offset = page * size;

        long totalElements = placeMapper.countMyFavorites(userId);

        List<PlaceListResponse> content = placeMapper.findMyFavorites(userId, size, offset)
                .stream()
                .map(PlaceListResponse::from)
                .toList();

        return PageResponse.of(content, page, size, totalElements);
    }

    private void validatePageRequest(int page, int size) {
        if (page < 0) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        if (size < 1 || size > 50) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }

    // 내 리뷰 조회
    public PageResponse<MyReviewListResponse> findMyReviews(Long userId, int page, int size) {
        validatePageRequest(page, size);

        int offset = page * size;

        long totalElements = reviewMapper.countMyReviews(userId);

        List<MyReviewListResponse> content = reviewMapper.findMyReviews(
                userId,
                size,
                offset
        );

        return PageResponse.of(content, page, size, totalElements);
    }

}
