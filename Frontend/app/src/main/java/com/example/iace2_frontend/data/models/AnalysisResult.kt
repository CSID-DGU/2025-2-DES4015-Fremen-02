package com.example.iace2_frontend.data.models

/**
 * 위험 레벨
 */
enum class RiskLevel {
    NORMAL,  // 정상 (안전)
    WARN,    // 경고 (의심)
    DANGER   // 위험 (스미싱)
}

/**
 * 메시지 분석 결과 데이터 모델
 * 
 * @param riskLevel 위험 레벨 (NORMAL, WARN, DANGER)
 * @param isDanger 위험 여부 (true: 위험/의심, false: 정상)
 * @param riskScore AI가 판단한 확신도 점수 (0~100)
 * @param category 감지된 스미싱 유형 (GAMBLING, IMPERSONATION, NORMAL)
 * @param reason LLM이 생성한 판단 근거
 * @param confidence 신뢰도 (레거시 호환용, riskScore 기반으로 생성)
 * @param sender 발신자 정보 분석 결과 (레거시 호환용)
 * @param content 내용 분석 결과 (레거시 호환용)
 * @param links 링크 분석 결과 (레거시 호환용)
 */
data class AnalysisResult(
    val riskLevel: RiskLevel,
    val isDanger: Boolean = false,
    val riskScore: Int? = null,
    val category: String? = null,
    val reason: String? = null,
    val confidence: String? = null,
    val sender: String? = null,
    val content: String? = null,
    val links: String? = null,
    val isSmishing: Boolean = false // 레거시 호환용
) {
    /**
     * 레거시 호환용: isSmishing 계산
     */
    val isSmishingComputed: Boolean
        get() = isDanger || isSmishing
}

