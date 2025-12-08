package com.example.iace2_frontend.data.repository

import com.example.iace2_frontend.data.api.ApiService
import com.example.iace2_frontend.data.api.RetrofitClient
import com.example.iace2_frontend.data.models.ApiResponse
import com.example.iace2_frontend.data.models.LoginRequest
import com.example.iace2_frontend.data.models.LoginResponse
import com.example.iace2_frontend.data.models.UserInfoResponse
import com.example.iace2_frontend.util.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 인증 관련 Repository
 * 
 * 로그인, 로그아웃, 회원 탈퇴 등의 인증 관련 API 호출을 담당합니다.
 */
class AuthRepository(
    private val apiService: ApiService = RetrofitClient.apiService,
    private val tokenManager: TokenManager = TokenManager
) {
    
    /**
     * 소셜 로그인
     * 
     * @param provider 소셜 로그인 제공자 ("kakao", "google", "naver")
     * @param accessToken 소셜 로그인 액세스 토큰
     * @return 로그인 결과 (성공 시 토큰 저장)
     */
    suspend fun login(provider: String, accessToken: String): Result<LoginResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val request = LoginRequest(provider, accessToken)
                val response = apiService.login(request)
                
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    if (apiResponse.success && apiResponse.data != null) {
                        // 토큰 저장
                        tokenManager.saveToken(apiResponse.data!!.token)
                        Result.success(apiResponse.data!!)
                    } else {
                        Result.failure(Exception(apiResponse.message ?: "로그인 실패"))
                    }
                } else {
                    Result.failure(Exception("서버 오류: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * 로그아웃
     * 
     * @return 성공 여부
     */
    suspend fun logout(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val token = tokenManager.getToken()
                if (token == null) {
                    return@withContext Result.failure(Exception("토큰이 없습니다"))
                }
                
                val response = apiService.logout("Bearer $token")
                
                if (response.isSuccessful) {
                    // 토큰 삭제
                    tokenManager.clearToken()
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("로그아웃 실패: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * 회원 탈퇴
     * 
     * @return 성공 여부
     */
    suspend fun withdraw(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val token = tokenManager.getToken()
                if (token == null) {
                    return@withContext Result.failure(Exception("토큰이 없습니다"))
                }
                
                val response = apiService.withdraw("Bearer $token")
                
                if (response.isSuccessful) {
                    // 토큰 삭제
                    tokenManager.clearToken()
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("회원 탈퇴 실패: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * 사용자 정보 조회
     * 
     * @return 사용자 정보
     */
    suspend fun getUserInfo(): Result<UserInfoResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val token = tokenManager.getToken()
                if (token == null) {
                    return@withContext Result.failure(Exception("토큰이 없습니다"))
                }
                
                val response = apiService.getUserInfo("Bearer $token")
                
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    if (apiResponse.success && apiResponse.data != null) {
                        Result.success(apiResponse.data!!)
                    } else {
                        Result.failure(Exception(apiResponse.message ?: "사용자 정보 조회 실패"))
                    }
                } else {
                    Result.failure(Exception("서버 오류: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * 현재 로그인 상태 확인
     * 
     * @return 로그인 여부
     */
    fun isLoggedIn(): Boolean {
        return tokenManager.getToken() != null
    }
}

