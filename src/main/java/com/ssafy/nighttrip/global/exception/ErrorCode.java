package com.ssafy.nighttrip.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // Common
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "COMMON_001", "입력값 중에 기준을 만족하지 않은 입력값이 있습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_002", "서버 내부 오류가 발생했습니다."),

    // Auth
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH_001", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "AUTH_002", "접근 권한이 없습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_003", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_004", "만료된 토큰입니다."),
    INVALID_TOKEN_TYPE(HttpStatus.UNAUTHORIZED, "AUTH_005", "올바르지 않은 토큰 타입입니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "AUTH_006", "이메일 또는 비밀번호가 올바르지 않습니다."),
    USER_INACTIVE(HttpStatus.FORBIDDEN, "AUTH_007", "활성화되지 않은 사용자입니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "AUTH_008", "Refresh Token이 존재하지 않습니다."),
    REFRESH_TOKEN_MISMATCH(HttpStatus.UNAUTHORIZED, "AUTH_009", "Refresh Token이 일치하지 않습니다."),
    // TODO 이메일 중복, 닉네임 중복

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_001", "존재하지 않는 사용자입니다."),
    PASSWORD_NOT_MATCHED(HttpStatus.BAD_REQUEST, "USER_002", "입력하신 비밀번호와 현재 비밀번호가 일치하지 않습니다."),

    // City
    CITY_NOT_FOUND(HttpStatus.NOT_FOUND, "CITY_001", "존재하지 않는 도시입니다."),

    // Favorite
    FAVORITE_ALREADY_EXISTS(HttpStatus.CONFLICT, "FAVORITE_001", "이미 즐겨찾기에 추가된 장소입니다."),
    FAVORITE_NOT_FOUND(HttpStatus.NOT_FOUND, "FAVORITE_002", "즐겨찾기에 추가되지 않은 장소입니다."),

    // Place
    PLACE_NOT_FOUND(HttpStatus.NOT_FOUND, "PLACE_001", "존재하지 않는 장소입니다."),

    // Course
    COURSE_NOT_FOUND(HttpStatus.NOT_FOUND, "COURSE_001", "존재하지 않는 코스입니다."),
    COURSE_ANALYSIS_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "COURSE_002", "코스 요청 분석에 실패했습니다."),
    COURSE_INVALID_ANALYSIS_RESULT(HttpStatus.INTERNAL_SERVER_ERROR, "COURSE_003", "코스 분석 결과 형식이 올바르지 않습니다."),
    COURSE_CANDIDATE_NOT_FOUND(HttpStatus.NOT_FOUND, "COURSE_004", "추천 가능한 장소 후보가 부족합니다."),
    MOBILITY_API_FAILED(HttpStatus.BAD_GATEWAY, "COURSE_005", "이동시간 계산에 실패했습니다."),
    INVALID_COURSE_COUNT(HttpStatus.BAD_REQUEST, "COURSE_006", "저장할 코스는 1개 이상 3개 이하이어야 합니다."),
    INVALID_COURSE_PLACE_COUNT(HttpStatus.BAD_REQUEST, "COURSE_007", "코스에는 최소 1개 이상의 장소가 포함되어야 합니다."),

    // Review
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "REVIEW_001", "존재하지 않는 리뷰입니다."),
    REVIEW_FORBIDDEN(HttpStatus.FORBIDDEN, "REVIEW_002", "리뷰에 대한 권한이 없습니다."),
    REVIEW_ALREADY_LIKED(HttpStatus.CONFLICT, "REVIEW_003", "이미 좋아요한 리뷰입니다."),
    REVIEW_LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "REVIEW_004", "좋아요하지 않은 리뷰입니다."),

    INVALID_IMAGE_OWNER(HttpStatus.BAD_REQUEST, "IMAGE_001", "유효하지 않은 프로필 이미지입니다."),
    INVALID_IMAGE_SIGNATURE(HttpStatus.BAD_REQUEST, "IMAGE_002", "이미지 업로드 검증에 실패했습니다.");


    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}