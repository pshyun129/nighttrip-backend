package com.ssafy.nighttrip.city.mapper;


import com.ssafy.nighttrip.city.domain.City;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CityMapper {

    List<City> findAll();

    City findById(@Param("cityId") Long cityId);


    String findConnectionCharset();

    // 코스
    Long findIdByName(@Param("name") String name);

    int existsById(@Param("cityId") Long cityId);
}
