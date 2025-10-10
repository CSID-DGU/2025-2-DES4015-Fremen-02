package com.example.iace2_frontend.data.models

/**
 * 메시지와 그에 대한 분석 결과를 함께 관리하는 데이터 클래스
 * 
 * @param id 메시지 고유 ID
 * @param message 사용자가 붙여넣은 메시지 내용
 * @param result 분석 결과 (분석 전에는 null)
 * @param isAnalyzing 현재 분석 중인지 여부
 */
data class MessageAnalysis(
    val id: Long,
    val message: String,
    val result: AnalysisResult? = null,
    val isAnalyzing: Boolean = false
)


