package com.example.iace2_frontend.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.example.iace2_frontend.data.repository.AnalysisRepository
import com.example.iace2_frontend.util.DeviceUtil
import com.example.iace2_frontend.util.NotificationUtil
import com.example.iace2_frontend.data.models.RiskLevel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

/**
 * SMS 분석 백그라운드 Worker
 * 
 * WorkManager를 통해 백그라운드에서 실행되며,
 * 마스킹된 메시지를 백엔드로 전송하고 결과를 받아 푸시 알림을 보냅니다.
 */
class SmsAnalysisWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    private val analysisRepository = AnalysisRepository()
    
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // 입력 데이터 가져오기
            val maskedMessage = inputData.getString("maskedMessage")
            val originalMessage = inputData.getString("originalMessage")
            val senderNumber = inputData.getString("senderNumber")
            val timestamp = inputData.getLong("timestamp", System.currentTimeMillis())
            
            if (maskedMessage.isNullOrEmpty() || senderNumber.isNullOrEmpty()) {
                return@withContext Result.failure()
            }
            
            // timestamp를 ISO 8601 형식으로 변환
            val receivedAt = timestampToISO8601(timestamp)
            
            // 백엔드로 분석 요청
            val result = analysisRepository.analyzeSms(
                context = applicationContext,
                sender = senderNumber,
                content = maskedMessage,
                receivedAt = receivedAt
            )
            
            result.fold(
                onSuccess = { analysisResult ->
                    // 위험(DANGER) 또는 경고(WARN) 문자만 알림 전송 (안전 문자는 알림 없음)
                    if (analysisResult.riskLevel == RiskLevel.DANGER || analysisResult.riskLevel == RiskLevel.WARN) {
                        val title = when (analysisResult.riskLevel) {
                            RiskLevel.DANGER -> "⚠️ 스미싱 위험 문자 감지"
                            RiskLevel.WARN -> "⚠️ 주의가 필요한 문자"
                            else -> "⚠️ 의심스러운 문자"
                        }
                        
                        val message = analysisResult.reason 
                            ?: when (analysisResult.riskLevel) {
                                RiskLevel.DANGER -> "스미싱일 가능성이 높습니다. 링크를 클릭하지 마세요!"
                                RiskLevel.WARN -> "주의가 필요한 메시지입니다. 링크 클릭에 유의하세요."
                                else -> "의심스러운 메시지가 감지되었습니다."
                            }
                        
                        // 푸시 알림 전송
                        NotificationUtil.showNotification(
                            context = applicationContext,
                            title = title,
                            message = message,
                            analysisResult = analysisResult,
                            originalMessage = originalMessage ?: "",
                            senderNumber = senderNumber
                        )
                    }
                    // 안전 문자(NORMAL)는 알림 없이 조용히 처리
                    
                    Result.success()
                },
                onFailure = { exception ->
                    // 에러 발생 시 로그만 남기고 실패 처리 (재시도 안 함)
                    android.util.Log.e(TAG, "분석 실패: ${exception.message}", exception)
                    Result.failure() // 한 번만 시도하고 실패 처리
                }
            )
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Worker 실행 중 오류: ${e.message}", e)
            Result.failure() // 한 번만 시도하고 실패 처리
        }
    }
    
    /**
     * timestamp를 ISO 8601 형식으로 변환
     */
    private fun timestampToISO8601(timestamp: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("Asia/Seoul")
        return sdf.format(Date(timestamp))
    }
    
    companion object {
        private const val TAG = "SmsAnalysisWorker"
    }
}

