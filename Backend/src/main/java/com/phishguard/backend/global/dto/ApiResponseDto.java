package com.phishguard.backend.global.dto;

import lombok.Getter;

@Getter
public class ApiResponseDto<T> {

    private final boolean success;
    private final T data;
    private final String message;

    public ApiResponseDto(boolean success, T data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }

    // 성공 시 데이터와 함께 반환
    public static <T> ApiResponseDto<T> success(T data) {
        return new ApiResponseDto<>(true, data, "요청이 성공적으로 처리되었습니다.");
    }

    // 성공이지만 데이터는 없을 때 (예: 삭제 완료)
    public static <T> ApiResponseDto<T> success() {
        return new ApiResponseDto<>(true, null, "요청이 성공적으로 처리되었습니다.");
    }
}
