package com.fesi6.team1.study_group.global.common.response;

import com.fesi6.team1.study_group.domain.meetup.dto.MeetupResponseDTO;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApiResponse<T> {

    private static final String SUCCESS_STATUS = "success";
    private static final String ERROR_STATUS = "error";
    private static final String TOKEN_ERROR_STATUS = "invalid-token";

    private String status;
    private T data;
    private String message;
    private Map<String, Object> additionalData;

    public static <T> ApiResponse<T> successResponse(T data) {
        return new ApiResponse<>(SUCCESS_STATUS, data, null);
    }

    public static ApiResponse<?> successWithNoContent() {
        return new ApiResponse<>(SUCCESS_STATUS, null, null);
    }

    public static ApiResponse<?> successWithMessage(String message) {
        return new ApiResponse<>(SUCCESS_STATUS, null, message);
    }

    public static <T> ApiResponse<T> successWithDataAndMessage(T data, String message) {
        return new ApiResponse<>(SUCCESS_STATUS, data, message);
    }

    public static ApiResponse<?> errorResponse(String message) {
        return new ApiResponse<>(ERROR_STATUS, null, message);
    }

    public static ApiResponse<?> tokenErrorResponse(String message) {
        return new ApiResponse<>(TOKEN_ERROR_STATUS, null, message);
    }

    private ApiResponse(String status, T data, String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    // successResponse 메소드 수정 - 추가 데이터도 전달할 수 있도록 변경
    public static <T> ApiResponse<T> successResponse(T data, Map<String, Object> additionalData) {
        return new ApiResponse<>(SUCCESS_STATUS, data, null, additionalData);
    }

    private ApiResponse(String status, T data, String message, Map<String, Object> additionalData) {
        this.status = status;
        this.data = data;
        this.message = message;
        this.additionalData = additionalData;
    }
}