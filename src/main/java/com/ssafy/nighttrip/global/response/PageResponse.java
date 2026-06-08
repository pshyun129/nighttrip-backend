package com.ssafy.nighttrip.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
/*
결과 조회용 응답 형식
페이징 처리
장소 목록, 즐겨찾기 목록, 리뷰 목록 등등
*/
@Getter
@AllArgsConstructor
public class PageResponse<T> {

    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;

    public static <T> PageResponse<T> of(
            List<T> content,
            int page,
            int size,
            long totalElements
    ) {
        int totalPages = size == 0 ? 0 : (int) Math.ceil((double) totalElements / size);

        return new PageResponse<>(
                content,
                page,
                size,
                totalElements,
                totalPages,
                page == 0,
                totalPages == 0 || page >= totalPages - 1
        );
    }
}