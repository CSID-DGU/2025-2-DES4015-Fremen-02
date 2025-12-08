package com.example.iace2_frontend.data.models

/**
 * API 공통 응답 래퍼
 * 
 * @param success 성공 여부
 * @param message 응답 메시지
 * @param data 응답 데이터
 * @param error 에러 정보 (선택적)
 */
data class ApiResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T? = null,
    val error: String? = null
)

