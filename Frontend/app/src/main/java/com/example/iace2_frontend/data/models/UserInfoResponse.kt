package com.example.iace2_frontend.data.models

/**
 * 사용자 정보 응답 DTO
 * 
 * @param userName 사용자 이름
 * @param userId 사용자 ID
 * @param email 이메일 (선택적)
 */
data class UserInfoResponse(
    val userName: String,
    val userId: String? = null,
    val email: String? = null
)

