package com.example.iace2_frontend.data.repository

import android.content.Context
import com.example.iace2_frontend.data.api.ApiService
import com.example.iace2_frontend.data.api.RetrofitClient
import com.example.iace2_frontend.data.models.AnalysisRequest
import com.example.iace2_frontend.data.models.AnalysisResult
import com.example.iace2_frontend.data.models.SmsAnalysisResponse
import com.example.iace2_frontend.util.DeviceUtil
import com.example.iace2_frontend.util.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 메시지 분석 관련 Repository
 * 
 * 메시지 분석 API 호출을 담당합니다.
 * 
 * 더미 모드: USE_MOCK_REPOSITORY = true로 설정하면 실제 API 호출 없이 더미 데이터를 반환합니다.
 */
object RepositoryConfig {
    // 더미 모드: true = 더미 데이터 사용, false = 실제 API 호출
    // TODO: 서버 연결 시 false로 변경
    const val USE_MOCK_REPOSITORY = true // 로컬 테스트용 (서버 닫혀있음)
}

class AnalysisRepository(
    private val apiService: ApiService = RetrofitClient.apiService,
    private val tokenManager: TokenManager = TokenManager,
    private val useMock: Boolean = RepositoryConfig.USE_MOCK_REPOSITORY
) {
    private val mockRepository = MockAnalysisRepository()
    
    /**
     * SMS 분석 (백그라운드에서 자동 호출)
     * 
     * @param context 컨텍스트 (device_uuid 가져오기 위해 필요)
     * @param sender 발신자 번호
     * @param content 마스킹된 메시지 내용
     * @param receivedAt 수신 시간 (ISO 8601 형식)
     * @return 분석 결과
     */
    suspend fun analyzeSms(
        context: Context,
        sender: String,
        content: String,
        receivedAt: String
    ): Result<AnalysisResult> {
        // 더미 모드인 경우 Mock Repository 사용
        if (useMock) {
            return mockRepository.analyzeSms(context, sender, content, receivedAt)
        }
        
        // TODO: 서버 연결 시 주석 해제
        /*
        return withContext(Dispatchers.IO) {
            try {
                val deviceUuid = DeviceUtil.getDeviceUuid(context)
                val request = AnalysisRequest(
                    deviceUuid = deviceUuid,
                    sender = sender,
                    content = content,
                    receivedAt = receivedAt
                )
                
                val response = apiService.analyzeSms(request)
                
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    if (apiResponse.success && apiResponse.data != null) {
                        val analysisResult = apiResponse.data!!.toAnalysisResult()
                        Result.success(analysisResult)
                    } else {
                        Result.failure(Exception(apiResponse.message ?: "분석 실패"))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = when (response.code()) {
                        400 -> "요청 데이터가 잘못되었습니다."
                        500 -> "서버 내부 오류가 발생했습니다."
                        else -> "서버 오류: ${response.code()}"
                    }
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                // 타임아웃 및 네트워크 에러 상세 로깅
                val errorMessage = when {
                    e.message?.contains("timeout", ignoreCase = true) == true -> {
                        "서버 응답 시간 초과 (3분 이상 소요). 서버 상태를 확인해주세요."
                    }
                    e.message?.contains("connection", ignoreCase = true) == true -> {
                        "서버 연결 실패. 네트워크 연결을 확인해주세요."
                    }
                    e.message?.contains("failed to connect", ignoreCase = true) == true -> {
                        "서버에 연결할 수 없습니다. 서버가 실행 중인지 확인해주세요."
                    }
                    else -> "네트워크 오류: ${e.message ?: "알 수 없는 오류"}"
                }
                android.util.Log.e("AnalysisRepository", "API 호출 실패: $errorMessage", e)
                Result.failure(Exception(errorMessage))
            }
        }
        */
        
        // 로컬 테스트용: Mock Repository 사용
        return mockRepository.analyzeSms(context, sender, content, receivedAt)
    }
    
    /**
     * 메시지 분석 (앱 내 붙여넣기용)
     * 
     * @param context 컨텍스트
     * @param message 분석할 메시지 내용
     * @return 분석 결과
     */
    suspend fun analyzeMessage(context: Context, message: String): Result<AnalysisResult> {
        // 더미 모드인 경우 Mock Repository 사용
        if (useMock) {
            return mockRepository.analyzeMessage(context, message)
        }
        
        // TODO: 서버 연결 시 주석 해제
        /*
        return withContext(Dispatchers.IO) {
            try {
                val deviceUuid = DeviceUtil.getDeviceUuid(context)
                val request = AnalysisRequest(
                    deviceUuid = deviceUuid,
                    sender = "앱 내 입력",
                    content = message,
                    receivedAt = DeviceUtil.getCurrentTimeISO8601()
                )
                
                val response = apiService.analyzeSms(request)
                
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    if (apiResponse.success && apiResponse.data != null) {
                        val analysisResult = apiResponse.data!!.toAnalysisResult()
                        Result.success(analysisResult)
                    } else {
                        Result.failure(Exception(apiResponse.message ?: "분석 실패"))
                    }
                } else {
                    val errorMessage = when (response.code()) {
                        400 -> "요청 데이터가 잘못되었습니다."
                        500 -> "서버 내부 오류가 발생했습니다."
                        else -> "서버 오류: ${response.code()}"
                    }
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                // 타임아웃 및 네트워크 에러 상세 로깅
                val errorMessage = when {
                    e.message?.contains("timeout", ignoreCase = true) == true -> {
                        "서버 응답 시간 초과 (3분 이상 소요). 서버 상태를 확인해주세요."
                    }
                    e.message?.contains("connection", ignoreCase = true) == true -> {
                        "서버 연결 실패. 네트워크 연결을 확인해주세요."
                    }
                    e.message?.contains("failed to connect", ignoreCase = true) == true -> {
                        "서버에 연결할 수 없습니다. 서버가 실행 중인지 확인해주세요."
                    }
                    else -> "네트워크 오류: ${e.message ?: "알 수 없는 오류"}"
                }
                android.util.Log.e("AnalysisRepository", "API 호출 실패: $errorMessage", e)
                Result.failure(Exception(errorMessage))
            }
        }
        */
        
        // 로컬 테스트용: Mock Repository 사용
        return mockRepository.analyzeMessage(context, message)
    }
}

