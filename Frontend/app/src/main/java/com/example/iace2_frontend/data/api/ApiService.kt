package com.example.iace2_frontend.data.api

import com.example.iace2_frontend.data.models.ApiResponse
import com.example.iace2_frontend.data.models.AnalysisRequest
import com.example.iace2_frontend.data.models.SmsAnalysisResponse
import com.example.iace2_frontend.data.models.LoginRequest
import com.example.iace2_frontend.data.models.LoginResponse
import com.example.iace2_frontend.data.models.UserInfoResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Header

/**
 * 백엔드 API 서비스 인터페이스
 */
interface ApiService {
    
    /**
     * SMS 분석 API (메시지 감지)
     * 
     * @param request 분석할 메시지 정보 (device_uuid, sender, content, received_at)
     * @return 분석 결과
     */
    @POST("api/v1/sms/check")
    suspend fun analyzeSms(
        @Body request: AnalysisRequest
    ): Response<SmsAnalysisResponse>
    
    /**
     * 메시지 분석 API (앱 내 붙여넣기용 - 레거시)
     * 
     * @param request 분석할 메시지 내용
     * @param token 인증 토큰 (선택적)
     * @return 분석 결과
     */
    @POST("api/message/analyze")
    suspend fun analyzeMessage(
        @Body request: AnalysisRequest,
        @Header("Authorization") token: String? = null
    ): Response<ApiResponse<SmsAnalysisResponse>>
    
    /**
     * 로그인 API
     * 
     * @param request 로그인 정보 (소셜 로그인 토큰 등)
     * @return 로그인 결과 (토큰, 사용자 정보 등)
     */
    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<ApiResponse<LoginResponse>>
    
    /**
     * 로그아웃 API
     * 
     * @param token 인증 토큰
     */
    @POST("api/auth/logout")
    suspend fun logout(
        @Header("Authorization") token: String
    ): Response<ApiResponse<Unit>>
    
    /**
     * 회원 탈퇴 API
     * 
     * @param token 인증 토큰
     */
    @POST("api/auth/withdraw")
    suspend fun withdraw(
        @Header("Authorization") token: String
    ): Response<ApiResponse<Unit>>
    
    /**
     * 사용자 정보 조회 API
     * 
     * @param token 인증 토큰
     * @return 사용자 정보
     */
    @GET("api/user/info")
    suspend fun getUserInfo(
        @Header("Authorization") token: String
    ): Response<ApiResponse<UserInfoResponse>>
}

