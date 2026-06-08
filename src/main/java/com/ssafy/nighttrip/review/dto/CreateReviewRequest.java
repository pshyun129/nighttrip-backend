package com.ssafy.nighttrip.review.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
public class CreateReviewRequest {

    @NotBlank(message = "리뷰 내용은 필수입니다.")
    @Size(max = 1000, message = "리뷰 내용은 1000자 이하이어야 합니다.")
    private String content;

    private LocalDate visitDate;

    private String visibility = "PUBLIC";

    private List<String> imageUrls;
}