package com.example.iace2_frontend.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.iace2_frontend.MainActivity
import com.example.iace2_frontend.R
import com.example.iace2_frontend.data.models.AnalysisResult
import com.example.iace2_frontend.util.RecentNotificationManager

/**
 * 로컬 알림 유틸리티
 * 
 * 경고/위험 문자 감지 시 푸시 알림을 표시합니다.
 */
object NotificationUtil {
    private const val CHANNEL_ID = "sms_analysis_channel"
    private const val CHANNEL_NAME = "문자 분석 알림"
    private const val NOTIFICATION_ID = 1001
    
    /**
     * 알림 채널 생성 (Android 8.0 이상 필요)
     */
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "스미싱 위험 문자 감지 시 알림을 받습니다."
                enableVibration(true)
                enableLights(true)
            }
            
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    /**
     * 분석 결과 알림 표시
     * 
     * @param context 컨텍스트
     * @param title 알림 제목
     * @param message 알림 메시지
     * @param analysisResult 분석 결과 (앱으로 이동 시 전달)
     * @param originalMessage 원본 메시지
     * @param senderNumber 발신자 번호
     */
    fun showNotification(
        context: Context,
        title: String,
        message: String,
        analysisResult: AnalysisResult,
        originalMessage: String,
        senderNumber: String
    ) {
        // 알림 채널 생성 (최초 1회)
        createNotificationChannel(context)
        
        // 앱으로 이동하는 Intent 생성 (상세 페이지로 이동)
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("showDetail", true)  // 상세 페이지 표시
            putExtra("originalMessage", originalMessage)
            putExtra("senderNumber", senderNumber)
            putExtra("riskLevel", analysisResult.riskLevel.name)
            putExtra("isDanger", analysisResult.isDanger)
            putExtra("riskScore", analysisResult.riskScore)
            putExtra("category", analysisResult.category)
            putExtra("reason", analysisResult.reason)
            putExtra("confidence", analysisResult.confidence)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // 위험 레벨에 따른 색상 결정
        val notificationColor = when (analysisResult.riskLevel.name) {
            "DANGER" -> 0xFFFF6B6B.toInt()  // 빨간색
            "WARN" -> 0xFFFFA726.toInt()     // 주황색
            else -> 0xFF4A9FF5.toInt()       // 파란색
        }
        
        // 메시지가 너무 길면 요약
        val shortMessage = if (message.length > 100) {
            message.take(100) + "..."
        } else {
            message
        }
        
        // 앱 로고를 Bitmap으로 변환 (큰 아이콘 및 작은 아이콘용)
        val appIcon = try {
            // 먼저 sentinel_logo를 시도
            BitmapFactory.decodeResource(
                context.resources,
                R.drawable.sentinel_logo
            ) ?: BitmapFactory.decodeResource(
                context.resources,
                R.mipmap.ic_launcher
            )
        } catch (e: Exception) {
            // 실패 시 기본 launcher 아이콘 사용
            BitmapFactory.decodeResource(
                context.resources,
                R.mipmap.ic_launcher
            )
        }
        
        // 알림 빌더 (예쁜 디자인)
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // 작은 아이콘 (단색 벡터, 앱 로고)
            .setLargeIcon(appIcon) // 큰 아이콘 (앱 로고)
            .setContentTitle(title)
            .setContentText(shortMessage)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(message) // 전체 메시지 표시
                    .setSummaryText("📱 발신자: $senderNumber")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setColor(notificationColor) // 위험 레벨에 따른 색상
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setShowWhen(true)
            .setWhen(System.currentTimeMillis())
            .build()
        
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
        
        // 최근 알림 데이터 저장
        RecentNotificationManager.saveRecentNotification(
            context = context,
            analysisResult = analysisResult,
            originalMessage = originalMessage,
            senderNumber = senderNumber
        )
    }
}

