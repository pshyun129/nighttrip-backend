//package com.ssafy.nighttrip.global.exception;
//
//import com.ssafy.nighttrip.global.response.ApiResponse;
//import com.ssafy.nighttrip.global.response.ErrorResponse;
//import com.ssafy.nighttrip.global.response.ValidationTestRequest;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.validation.Valid;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
//@RestControllerAdvice
//public class GlobalExceptionHandler {
//
//    @ExceptionHandler(BusinessException.class)
//    public ResponseEntity<ApiResponse<Void>> handleBusinessException(
//            BusinessException e,
//            HttpServletRequest request
//    ) {
//        ErrorCode errorCode = e.getErrorCode();
//
//        ErrorResponse errorResponse = ErrorResponse.of(errorCode.getCode());
//
//        return ResponseEntity
//                .status(errorCode.getStatus())
//                .body(ApiResponse.fail(
//                        errorCode.getStatus(),
//                        errorCode.getMessage(),
//                        errorResponse,
//                        request
//                ));
//    }
//
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ApiResponse<Void>> handleException(
//            Exception e,
//            HttpServletRequest request
//    ) {
//        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
//
//        ErrorResponse errorResponse = ErrorResponse.of(errorCode.getCode());
//        return ResponseEntity
//                .status(errorCode.getStatus())
//                .body(ApiResponse.fail(
//                        errorCode.getStatus(),
//                        errorCode.getMessage(),
//                        errorResponse,
//                        request
//                ));
//    }
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(
//            MethodArgumentNotValidException e,
//            HttpServletRequest request
//    ) {
//        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
//
//        ErrorResponse errorResponse = ErrorResponse.of(errorCode.getCode());
//
//        return ResponseEntity
//                .status(errorCode.getStatus())
//                .body(ApiResponse.fail(
//                        errorCode.getStatus(),
//                        errorCode.getMessage(),
//                        errorResponse,
//                        request
//                ));
//    }
//
//
//
//}


package com.ssafy.nighttrip.global.exception;

import com.ssafy.nighttrip.global.response.ApiResponse;
import com.ssafy.nighttrip.global.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(
            BusinessException e,
            HttpServletRequest request
    ) {
        ErrorCode errorCode = e.getErrorCode();

        log.warn("BusinessException 발생. path={}, code={}, message={}",
                request.getRequestURI(),
                errorCode.getCode(),
                errorCode.getMessage()
        );

        ErrorResponse errorResponse = ErrorResponse.of(errorCode.getCode());

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.fail(
                        errorCode.getStatus(),
                        errorCode.getMessage(),
                        errorResponse,
                        request
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e,
            HttpServletRequest request
    ) {
        e.getBindingResult().getFieldErrors().forEach(error ->
                log.warn("Validation 실패. path={}, field={}, rejectedValue={}, message={}",
                        request.getRequestURI(),
                        error.getField(),
                        error.getRejectedValue(),
                        error.getDefaultMessage()
                )
        );

        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;

        ErrorResponse errorResponse = ErrorResponse.of(errorCode.getCode());

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.fail(
                        errorCode.getStatus(),
                        errorCode.getMessage(),
                        errorResponse,
                        request
                ));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e,
            HttpServletRequest request
    ) {
        log.warn("Request Body 파싱 실패. path={}, message={}",
                request.getRequestURI(),
                e.getMessage()
        );

        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;

        ErrorResponse errorResponse = ErrorResponse.of(errorCode.getCode());

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.fail(
                        errorCode.getStatus(),
                        errorCode.getMessage(),
                        errorResponse,
                        request
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(
            Exception e,
            HttpServletRequest request
    ) {
        log.error("서버 내부 오류 발생. path={}, message={}",
                request.getRequestURI(),
                e.getMessage(),
                e
        );

        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

        ErrorResponse errorResponse = ErrorResponse.of(errorCode.getCode());

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.fail(
                        errorCode.getStatus(),
                        errorCode.getMessage(),
                        errorResponse,
                        request
                ));
    }
}