package com.example.iace2_frontend.data.models

/**
 * 로그인 응답 DTO
 * 
 * @param token 서버에서 발급한 인증 토큰
 * @param userName 사용자 이름
 * @param userId 사용자 ID
 */
data class LoginResponse(
    val token: String,
    val userName: String,
    val userId: String? = null
)

