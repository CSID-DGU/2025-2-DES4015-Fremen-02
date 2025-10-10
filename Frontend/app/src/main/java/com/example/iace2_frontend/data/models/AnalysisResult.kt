package com.example.iace2_frontend.data.models

/**
 * 메시지 분석 결과 데이터 모델
 * 
 * @param isSmishing 스미싱 여부 (true: 스미싱, false: 정상)
 * @param confidence 신뢰도 ("높아요", "보통", "낮아요")
 * @param sender 발신자 정보 분석 결과
 * @param content 내용 분석 결과
 * @param links 링크 분석 결과
 * @param category 분류 결과 메시지
 */
data class AnalysisResult(
    val isSmishing: Boolean,
    val confidence: String,
    val sender: String?,
    val content: String?,
    val links: String?,
    val category: String?
)

