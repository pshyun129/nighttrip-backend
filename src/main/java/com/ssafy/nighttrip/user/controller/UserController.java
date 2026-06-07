package com.ssafy.nighttrip.user.controller;

import com.ssafy.nighttrip.global.response.ApiResponse;
import com.ssafy.nighttrip.user.dto.DeleteMyInfoRequest;
import com.ssafy.nighttrip.user.dto.MyInfoResponse;
import com.ssafy.nighttrip.user.dto.UpdateMyInfoRequest;
import com.ssafy.nighttrip.user.dto.UpdateMyPasswordRequest;
import com.ssafy.nighttrip.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;


    // 내 정보 조회
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MyInfoResponse>> getMyInfo(
            Authentication authentication,
            HttpServletRequest request
        ){

        Long userId = (Long) authentication.getPrincipal();

        MyInfoResponse myInfoResponse = userService.getMyInfo(userId);

        return ResponseEntity
                .status(200)
                .body(ApiResponse.success(
                        HttpStatus.OK,
                        "내 정보 조회 성공",
                         myInfoResponse,
                        request

                        ));

    }


    // 내 정보 수정
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<Void>> updateMyInfo(
            @Valid @RequestBody UpdateMyInfoRequest updateMyInfoRequest,
            Authentication authentication,
            HttpServletRequest request
        ){
        Long userId = (Long) authentication.getPrincipal();

        userService.updateMyInfo(userId, updateMyInfoRequest);

        MyInfoResponse myInfoResponse = userService.getMyInfo(userId);

        return ResponseEntity
                .status(200)
                .body(ApiResponse.success(
                        HttpStatus.OK,
                        "닉네임 변경 성공",
                        request

                ));



    }

    // 회원 탈퇴
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> deleteMyInfo(
            @Valid @RequestBody DeleteMyInfoRequest deleteMyInfoRequest,
            Authentication authentication,
            HttpServletRequest request
            ){
        Long userId = (Long) authentication.getPrincipal();

        userService.deleteMyInfo(userId, deleteMyInfoRequest);

        return ResponseEntity
                .status(200)
                .body(ApiResponse.success(
                        HttpStatus.OK,
                        "회원 탈퇴 성공",
                        request
                ));

    }

    // 비밀번호 수정
    @PatchMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> updatePassword(
            @Valid @RequestBody UpdateMyPasswordRequest updateMyPasswordRequest,
            Authentication authentication,
            HttpServletRequest request
            ){

        Long userId = (Long) authentication.getPrincipal();

        userService.updatePassword(userId, updateMyPasswordRequest);

        return ResponseEntity
                .status(200)
                .body(ApiResponse.success(
                        HttpStatus.OK,
                        "비밀번호가 변경되었습니다",
                        request

                ));
    }


}
