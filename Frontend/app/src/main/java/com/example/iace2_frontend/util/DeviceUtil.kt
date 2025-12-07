package com.example.iace2_frontend.util

import android.content.Context
import android.provider.Settings
import java.text.SimpleDateFormat
import java.util.*

/**
 * 기기 관련 유틸리티
 */
object DeviceUtil {
    
    /**
     * Android ID를 가져옵니다 (기기 식별값)
     * 
     * @param context 컨텍스트
     * @return Android ID
     */
    fun getDeviceUuid(context: Context): String {
        return Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        ) ?: "unknown_device_${System.currentTimeMillis()}"
    }
    
    /**
     * 현재 시간을 ISO 8601 형식으로 반환
     * 
     * @return ISO 8601 형식의 시간 문자열 (예: "2025-11-09T19:05:00")
     */
    fun getCurrentTimeISO8601(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("Asia/Seoul")
        return sdf.format(Date())
    }
}

