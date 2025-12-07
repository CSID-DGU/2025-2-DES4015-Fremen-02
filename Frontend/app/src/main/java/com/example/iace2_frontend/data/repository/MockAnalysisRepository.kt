package com.example.iace2_frontend.data.repository

import android.content.Context
import com.example.iace2_frontend.data.models.AnalysisResult
import com.example.iace2_frontend.data.models.RiskLevel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/**
 * 더미 데이터를 사용하는 Mock Repository
 * 
 * 백엔드 연동 전 로컬 테스트용
 * 실제 API 호출 없이 더미 응답을 반환합니다.
 */
class MockAnalysisRepository {
    
    /**
     * SMS 분석 (더미 응답)
     * 
     * 메시지 내용에 따라 다른 위험도를 반환합니다.
     */
    suspend fun analyzeSms(
        context: Context,
        sender: String,
        content: String,
        receivedAt: String
    ): Result<AnalysisResult> {
        return withContext(Dispatchers.IO) {
            // 네트워크 지연 시뮬레이션 (1~2초)
            delay((1000..2000).random().toLong())
            
            // 메시지 내용에 따라 위험도 판단 (더미 로직)
            val analysisResult = generateDummyResult(content, sender)
            Result.success(analysisResult)
        }
    }
    
    /**
     * 메시지 분석 (앱 내 붙여넣기용, 더미 응답)
     */
    suspend fun analyzeMessage(context: Context, message: String): Result<AnalysisResult> {
        return withContext(Dispatchers.IO) {
            // 네트워크 지연 시뮬레이션 (1~2초)
            delay((1000..2000).random().toLong())
            
            val analysisResult = generateDummyResult(message, "앱 내 입력")
            Result.success(analysisResult)
        }
    }
    
    /**
     * 메시지 내용을 분석하여 더미 결과 생성
     * 
     * 키워드 기반으로 위험도를 판단합니다.
     */
    private fun generateDummyResult(content: String, sender: String): AnalysisResult {
        val lowerContent = content.lowercase()
        
        // 위험 키워드 체크
        val dangerKeywords = listOf(
            "casino", "도박", "100%", "보장", "확정", "당첨", "상금",
            "계좌", "비밀번호", "인증번호", "입력", "확인", "급함",
            "지금", "즉시", "마감", "한정", "무료", "이벤트"
        )
        
        val warningKeywords = listOf(
            "링크", "url", "클릭", "접속", "확인", "문자", "알림"
        )
        
        val dangerCount = dangerKeywords.count { lowerContent.contains(it) }
        val warningCount = warningKeywords.count { lowerContent.contains(it) }
        
        return when {
            // 위험: 도박 키워드 2개 이상 또는 특정 패턴
            lowerContent.contains("casino") || 
            lowerContent.contains("도박") || 
            (lowerContent.contains("100%") && lowerContent.contains("보장")) ||
            dangerCount >= 3 -> {
                AnalysisResult(
                    riskLevel = RiskLevel.DANGER,
                    isDanger = true,
                    riskScore = (85..98).random(),
                    category = "GAMBLING",
                    reason = "도박 키워드 + 과장문구(100% 보장) + 고위험 도메인 패턴 감지",
                    confidence = "매우 높아요",
                    sender = sender,
                    content = "개인정보 및 금융정보 요구 확인",
                    isSmishing = true
                )
            }
            
            // 경고: 의심스러운 키워드 포함 (알림 표시)
            lowerContent.contains("계좌") && lowerContent.contains("확인") ||
            lowerContent.contains("kb국민카드") ||
            dangerCount >= 2 || warningCount >= 2 -> {
                AnalysisResult(
                    riskLevel = RiskLevel.WARN,
                    isDanger = true,  // 경고도 알림 표시
                    riskScore = (50..75).random(),
                    category = "IMPERSONATION",
                    reason = "일부 의심스러운 문구가 포함되어 있습니다. 링크 클릭에 주의하세요.",
                    confidence = "보통",
                    sender = sender,
                    content = "의심스러운 문구 포함",
                    isSmishing = false
                )
            }
            
            // 정상: 위험 키워드 없음
            else -> {
                AnalysisResult(
                    riskLevel = RiskLevel.NORMAL,
                    isDanger = false,
                    riskScore = (5..25).random(),
                    category = "NORMAL",
                    reason = "합법적인 광고/홍보성 문자로 분류되었습니다.",
                    confidence = "높아요",
                    sender = sender,
                    content = "개인정보 요구-위험 문구 없음",
                    isSmishing = false
                )
            }
        }
    }
}

