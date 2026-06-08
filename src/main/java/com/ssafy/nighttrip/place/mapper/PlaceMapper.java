package com.ssafy.nighttrip.place.mapper;

import com.ssafy.nighttrip.place.dto.PlaceDetailRow;
import com.ssafy.nighttrip.place.dto.PlaceListRow;
import com.ssafy.nighttrip.place.dto.PlaceSearchCondition;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PlaceMapper {

    List<PlaceListRow> findPlaces(PlaceSearchCondition condition);

    long countPlaces(PlaceSearchCondition condition);

    PlaceDetailRow findPlaceDetailById(@Param("placeId") Long placeId);

    int existsById(@Param("placeId") Long placeId);

    int existsFavorite(
            @Param("userId") Long userId,
            @Param("placeId") Long placeId
    );

    int insertFavorite(
            @Param("userId") Long userId,
            @Param("placeId") Long placeId
    );

    int deleteFavorite(
            @Param("userId") Long userId,
            @Param("placeId") Long placeId
    );

    List<PlaceListRow> findMyFavorites(
            @Param("userId") Long userId,
            @Param("size") int size,
            @Param("offset") int offset
    );

    long countMyFavorites(@Param("userId") Long userId);

}