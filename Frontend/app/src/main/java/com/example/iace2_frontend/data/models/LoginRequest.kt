package com.example.iace2_frontend.data.models

/**
 * 로그인 요청 DTO
 * 
 * @param provider 소셜 로그인 제공자 ("kakao", "google", "naver")
 * @param accessToken 소셜 로그인 액세스 토큰
 */
data class LoginRequest(
    val provider: String,
    val accessToken: String
)

