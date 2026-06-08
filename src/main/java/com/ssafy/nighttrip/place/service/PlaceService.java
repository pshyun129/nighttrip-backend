package com.ssafy.nighttrip.place.service;

import com.ssafy.nighttrip.global.exception.BusinessException;
import com.ssafy.nighttrip.global.exception.ErrorCode;
import com.ssafy.nighttrip.global.response.PageResponse;
import com.ssafy.nighttrip.place.domain.Place;
import com.ssafy.nighttrip.place.dto.*;
import com.ssafy.nighttrip.place.mapper.PlaceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaceService  {

    private final PlaceMapper placeMapper;

    // 필터링 기반 검색
    public PageResponse<PlaceListResponse> findPlaces(PlaceSearchRequest request) {
        int page = request.pageOrDefault();
        int size = request.sizeOrDefault();


        validatePageRequest(page, size);

        int offset = page * size;

        PlaceSearchCondition condition = new PlaceSearchCondition(
                request.categoryOrNull(),
                request.keywordOrNull(),
                size,
                offset
        );

        long totalElements = placeMapper.countPlaces(condition);

        List<PlaceListResponse> content = placeMapper.findPlaces(condition)
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

    // 장소 상세 내용
    public PlaceDetailResponse findPlaceDetail(Long placeId) {
        PlaceDetailRow row = placeMapper.findPlaceDetailById(placeId);

        if (row == null) {
            throw new BusinessException(ErrorCode.PLACE_NOT_FOUND);
        }

        return PlaceDetailResponse.from(row);
    }

    // 즐겨찾기 추가
    public void addFavoritePlace(Long userId, Long placeId) {

        if(placeMapper.existsById(placeId) == 0){
            throw new BusinessException(ErrorCode.PLACE_NOT_FOUND);
        }

        if(placeMapper.existsFavorite(userId, placeId) != 0){
            throw new BusinessException(ErrorCode.FAVORITE_ALREADY_EXISTS);
        }

        placeMapper.insertFavorite(userId, placeId);

    }

    // 즐겨찾기 제거
    public void removeFavoritePlace(Long userId, Long placeId) {

        if(placeMapper.existsById(placeId) == 0){
            throw new BusinessException(ErrorCode.PLACE_NOT_FOUND);
        }
        int row = placeMapper.deleteFavorite(userId, placeId);

        if(row == 0){
            throw new BusinessException(ErrorCode.FAVORITE_NOT_FOUND);
        }

    }


}