package com.ssafy.nighttrip.city.service;

import com.ssafy.nighttrip.city.domain.City;
import com.ssafy.nighttrip.city.dto.CityResponse;
import com.ssafy.nighttrip.city.mapper.CityMapper;
import com.ssafy.nighttrip.global.exception.BusinessException;
import com.ssafy.nighttrip.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CityService {

    private final CityMapper cityMapper;

    // 전체 도시 목록 불러오기
    public List<CityResponse> findAll() {
        List<City> cities = cityMapper.findAll();

        List<CityResponse> responses = new ArrayList<>();
        System.out.println("connection charset = " + cityMapper.findConnectionCharset());

        for (City city : cities) {
            System.out.println("city name = " + city.getName());
            System.out.println("city region = " + city.getRegion());

            responses.add(CityResponse.from(city));
        }
        return responses;
    }

    // 세부 도시 정보 불러오기
    public CityResponse findById(Long cityId) {
        City city = cityMapper.findById(cityId);

        if (city == null) {
            throw new BusinessException(ErrorCode.CITY_NOT_FOUND);
        }

        return CityResponse.from(city);
    }




}
