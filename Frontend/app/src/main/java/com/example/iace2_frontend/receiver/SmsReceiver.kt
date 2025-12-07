package com.example.iace2_frontend.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.telephony.SmsMessage
import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.iace2_frontend.work.SmsAnalysisWorker
import com.example.iace2_frontend.util.DataMaskingUtil
import java.util.concurrent.TimeUnit

/**
 * SMS 수신 BroadcastReceiver
 * 
 * 문자 메시지를 수신하면 WorkManager를 통해 백그라운드에서 분석 작업을 실행합니다.
 */
class SmsReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            
            for (smsMessage in messages) {
                val messageBody = smsMessage.messageBody
                val senderNumber = smsMessage.originatingAddress
                
                Log.d(TAG, "SMS 수신: 발신자=$senderNumber, 내용=$messageBody")
                
                // 개인정보 마스킹 처리
                val maskedMessage = DataMaskingUtil.maskSensitiveData(messageBody)
                
                Log.d(TAG, "마스킹된 메시지: $maskedMessage")
                
                // WorkManager를 통해 백그라운드에서 분석 작업 실행
                val inputData = Data.Builder()
                    .putString("originalMessage", messageBody)
                    .putString("maskedMessage", maskedMessage)
                    .putString("senderNumber", senderNumber)
                    .putLong("timestamp", System.currentTimeMillis())
                    .build()
                
                val workRequest = OneTimeWorkRequestBuilder<SmsAnalysisWorker>()
                    .setInputData(inputData)
                    // 재시도 비활성화: 실패 시 재시도하지 않음
                    .setBackoffCriteria(
                        BackoffPolicy.EXPONENTIAL,
                        0,
                        TimeUnit.SECONDS
                    )
                    .build()
                
                // Unique work로 중복 요청 방지: 같은 작업이 이미 실행 중이면 새로 추가하지 않음
                WorkManager.getInstance(context)
                    .enqueueUniqueWork(
                        "SMS_ANALYSIS_${senderNumber}_${System.currentTimeMillis()}",
                        ExistingWorkPolicy.KEEP,  // 이미 실행 중이면 새 요청 무시
                        workRequest
                    )
                
                Log.d(TAG, "분석 작업이 WorkManager에 등록되었습니다.")
            }
        }
    }
    
    companion object {
        private const val TAG = "SmsReceiver"
    }
}

