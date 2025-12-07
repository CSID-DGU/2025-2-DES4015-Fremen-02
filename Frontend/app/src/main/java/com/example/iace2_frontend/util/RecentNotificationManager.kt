package com.example.iace2_frontend.util

import android.content.Context
import android.content.SharedPreferences
import com.example.iace2_frontend.data.models.AnalysisResult
import com.example.iace2_frontend.data.models.RiskLevel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * 최근 알림 데이터 관리 유틸리티
 * 
 * 여러 알림을 리스트로 저장하고 불러옵니다.
 */
object RecentNotificationManager {
    private const val PREFS_NAME = "recent_notification_prefs"
    private const val KEY_NOTIFICATIONS = "notifications"
    private const val MAX_NOTIFICATIONS = 100 // 최대 저장 개수
    private val gson = Gson()
    
    /**
     * 최근 알림 데이터 저장 (리스트에 추가)
     */
    fun saveRecentNotification(
        context: Context,
        analysisResult: AnalysisResult,
        originalMessage: String,
        senderNumber: String
    ) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val notificationsJson = prefs.getString(KEY_NOTIFICATIONS, null)
        
        val notifications: MutableList<RecentNotificationData> = if (notificationsJson != null) {
            val type = object : TypeToken<List<RecentNotificationData>>() {}.type
            val list = gson.fromJson<List<RecentNotificationData>>(notificationsJson, type)
            if (list != null) {
                list.toMutableList()
            } else {
                mutableListOf()
            }
        } else {
            mutableListOf()
        }
        
        // 새 알림 추가 (최신이 앞에 오도록)
        val newNotification = RecentNotificationData(
            id = System.currentTimeMillis(),
            analysisResult = analysisResult,
            originalMessage = originalMessage,
            senderNumber = senderNumber,
            timestamp = System.currentTimeMillis()
        )
        
        notifications.add(0, newNotification)
        
        // 최대 개수 제한
        if (notifications.size > MAX_NOTIFICATIONS) {
            notifications.removeAt(notifications.size - 1)
        }
        
        // 저장
        prefs.edit().putString(KEY_NOTIFICATIONS, gson.toJson(notifications)).apply()
    }
    
    /**
     * 모든 알림 목록 불러오기 (날짜/시간 내림차순 정렬)
     */
    fun getAllNotifications(context: Context): List<RecentNotificationData> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val notificationsJson = prefs.getString(KEY_NOTIFICATIONS, null) ?: return emptyList()
        
        val type = object : TypeToken<List<RecentNotificationData>>() {}.type
        val notifications = gson.fromJson<List<RecentNotificationData>>(notificationsJson, type) ?: return emptyList()
        
        // 타임스탬프 기준 내림차순 정렬 (최신순)
        return notifications.sortedByDescending { it.timestamp }
    }
    
    /**
     * 최근 알림 데이터 불러오기 (가장 최신 것 하나)
     */
    fun getRecentNotification(context: Context): RecentNotificationData? {
        return getAllNotifications(context).firstOrNull()
    }
    
    /**
     * 최근 알림 데이터가 있는지 확인
     */
    fun hasRecentNotification(context: Context): Boolean {
        return getAllNotifications(context).isNotEmpty()
    }
    
    /**
     * 특정 알림 삭제
     */
    fun deleteNotification(context: Context, notificationId: Long) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val notificationsJson = prefs.getString(KEY_NOTIFICATIONS, null) ?: return
        
        val type = object : TypeToken<MutableList<RecentNotificationData>>() {}.type
        val notifications = gson.fromJson<MutableList<RecentNotificationData>>(notificationsJson, type) ?: return
        
        notifications.removeAll { it.id == notificationId }
        
        prefs.edit().putString(KEY_NOTIFICATIONS, gson.toJson(notifications)).apply()
    }
    
    /**
     * 모든 알림 데이터 삭제
     */
    fun clearRecentNotification(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_NOTIFICATIONS).apply()
    }
}

/**
 * 최근 알림 데이터 모델
 */
data class RecentNotificationData(
    val id: Long = System.currentTimeMillis(),
    val analysisResult: AnalysisResult,
    val originalMessage: String,
    val senderNumber: String,
    val timestamp: Long = System.currentTimeMillis()
)

