package com.ssafy.nighttrip.city.dto;

import com.ssafy.nighttrip.city.domain.City;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CityResponse {

    private Long cityId;
    private String name;
    private String region;
    private String description;
    private String imageUrl;

    public static CityResponse from(City city) {
        return new CityResponse(
                city.getCityId(),
                city.getName(),
                city.getRegion(),
                city.getDescription(),
                city.getImageUrl()
        );
    }

}
