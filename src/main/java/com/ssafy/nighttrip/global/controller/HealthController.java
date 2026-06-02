package com.ssafy.nighttrip.global.controller;

import com.ssafy.nighttrip.global.exception.BusinessException;
import com.ssafy.nighttrip.global.exception.ErrorCode;
import com.ssafy.nighttrip.global.response.ApiResponse;
import com.ssafy.nighttrip.global.response.ValidationTestRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    // 서버 동작 테스트 + 공통 응답 형식 테스트
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health(HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(
                        HttpStatus.OK,
                        "서버 상태 확인 성공",
                        "OK",
                        request
                ));
    }

    // businessException globalExceptionHandler 테스트
    @GetMapping("/health/business-error")
    public ResponseEntity<ApiResponse<Void>> businessError() {
        throw new BusinessException(ErrorCode.USER_NOT_FOUND);
    }

    // runtimeException globalExceptionHandler 테스트
    @GetMapping("/health/server-error")
    public ResponseEntity<ApiResponse<Void>> serverError() {
        throw new RuntimeException("테스트용 서버 예외입니다.");
    }

    // validation 테스트
    @PostMapping("/health/validation-error")
    public ResponseEntity<ApiResponse<String>> validationError(
            @Valid @RequestBody ValidationTestRequest requestDto,
            HttpServletRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(
                        HttpStatus.OK,
                        "검증 성공",
                        requestDto.getName(),
                        request
                ));
    }
}