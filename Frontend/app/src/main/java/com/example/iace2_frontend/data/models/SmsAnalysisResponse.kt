package com.example.iace2_frontend.data.models

/**
 * SMS 분석 응답 DTO (실제 백엔드 API 스펙)
 * 
 * 실제 서버 응답 형식:
 * {
 *   "success": true,
 *   "data": {
 *     "riskLevel": "CRITICAL",
 *     "category": "SMISHING",
 *     "danger": true,
 *     "score": 99.95031356811523,
 *     "explanation": "..."
 *   },
 *   "message": "요청이 성공적으로 처리되었습니다."
 * }
 */
data class SmsAnalysisResponse(
    val success: Boolean,
    val data: SmsAnalysisData? = null,
    val message: String? = null
)

/**
 * SMS 분석 결과 데이터
 */
data class SmsAnalysisData(
    val riskLevel: String? = null,  // "CRITICAL", "HIGH", "MEDIUM", "SAFE"
    val category: String? = null,    // "SMISHING", "GAMBLING", "IMPERSONATION", "NORMAL"
    val danger: Boolean,
    val score: Double,
    val explanation: String? = null
) {
    /**
     * SmsAnalysisData를 AnalysisResult로 변환
     */
    fun toAnalysisResult(): AnalysisResult {
        // score를 0-100 범위로 변환
        val riskScore = score.toInt()
        
        // riskLevel 기반으로 판단
        val riskLevelEnum = when {
            riskLevel != null -> {
                when (riskLevel.uppercase()) {
                    "CRITICAL", "HIGH" -> RiskLevel.DANGER
                    "MEDIUM" -> RiskLevel.WARN
                    "SAFE", "LOW" -> RiskLevel.NORMAL
                    else -> RiskLevel.DANGER
                }
            }
            score >= 80 -> RiskLevel.DANGER
            score >= 50 -> RiskLevel.WARN
            else -> RiskLevel.NORMAL
        }
        
        // confidence 문자열 생성
        val confidence = when {
            riskScore >= 80 -> "매우 높아요"
            riskScore >= 50 -> "높아요"
            riskScore >= 30 -> "보통"
            else -> "낮아요"
        }
        
        // explanation이 null이면 기본 메시지
        val reasonText = explanation ?: when {
            danger -> "의심스러운 메시지가 감지되었습니다."
            else -> "정상적인 메시지로 판단됩니다."
        }
        
        return AnalysisResult(
            riskLevel = riskLevelEnum,
            isDanger = danger,
            riskScore = riskScore,
            category = category ?: "SMISHING",
            reason = reasonText,
            confidence = confidence,
            isSmishing = danger
        )
    }
}

