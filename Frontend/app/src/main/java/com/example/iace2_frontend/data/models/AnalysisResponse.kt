package com.example.iace2_frontend.data.models

/**
 * 메시지 분석 응답 DTO
 * 
 * 백엔드/AI 팀원과 협의하여 필드를 조정할 수 있습니다.
 * 
 * @param riskLevel 위험 레벨 (NORMAL, WARN, DANGER 또는 "NORMAL", "WARN", "DANGER" 문자열)
 * @param isSmishing 스미싱 여부
 * @param confidence 신뢰도 (0.0 ~ 1.0 또는 "높아요", "보통", "낮아요" 문자열)
 * @param sender 발신자 정보 분석 결과
 * @param content 내용 분석 결과
 * @param links 링크 분석 결과
 * @param category 분류 결과 메시지
 */
data class AnalysisResponse(
    val riskLevel: String, // "NORMAL", "WARN", "DANGER"
    val isSmishing: Boolean,
    val confidence: String,
    val sender: String?,
    val content: String?,
    val links: String?,
    val category: String?
) {
    /**
     * AnalysisResponse를 AnalysisResult로 변환
     */
    fun toAnalysisResult(): AnalysisResult {
        val risk = when (riskLevel.uppercase()) {
            "NORMAL" -> RiskLevel.NORMAL
            "WARN" -> RiskLevel.WARN
            "DANGER" -> RiskLevel.DANGER
            else -> RiskLevel.NORMAL
        }
        
        return AnalysisResult(
            riskLevel = risk,
            isSmishing = isSmishing,
            confidence = confidence,
            sender = sender,
            content = content,
            links = links,
            category = category
        )
    }
}

