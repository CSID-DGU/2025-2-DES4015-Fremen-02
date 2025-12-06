package com.example.iace2_frontend.data.models

import com.google.gson.annotations.SerializedName

/**
 * 메시지 분석 요청 DTO
 * 
 * 백엔드 API 스펙에 맞춰 JSON 키 이름을 카멜케이스로 매핑합니다.
 * 
 * @param deviceUuid 기기 식별값 (Android ID) - JSON: "deviceUuid"
 * @param sender 문자를 보낸 발신 번호 - JSON: "sender"
 * @param content OCR 및 특수문자 제거 후 정제된 텍스트 (마스킹 처리됨) - JSON: "content"
 * @param receivedAt 문자가 수신된 시간 (ISO 8601 형식) - JSON: "received_at"
 */
data class AnalysisRequest(
    @SerializedName("deviceUuid")
    val deviceUuid: String,
    
    @SerializedName("sender")
    val sender: String,
    
    @SerializedName("content")
    val content: String,
    
    @SerializedName("received_at")
    val receivedAt: String
)

