package com.example.iace2_frontend.util

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.iace2_frontend.work.SmsAnalysisWorker
import java.util.concurrent.TimeUnit

/**
 * 테스트용 유틸리티
 * 
 * 실제 SMS 수신 없이 테스트 메시지를 시뮬레이션할 수 있습니다.
 */
object TestUtil {
    private var isRequesting = false
    
    /**
     * 테스트용 SMS 분석 시뮬레이션
     * 
     * 실제 SMS 수신 없이 분석 작업을 테스트할 수 있습니다.
     * 중복 요청 방지: 이미 요청 중이면 무시합니다.
     * 
     * @param context 컨텍스트
     * @param sender 발신자 번호 (예: "010-1234-5678")
     * @param message 메시지 내용
     */
    fun simulateSmsAnalysis(
        context: Context,
        sender: String,
        message: String
    ) {
        // 중복 요청 방지
        if (isRequesting) return
        isRequesting = true
        
        // 마스킹 처리
        val maskedMessage = DataMaskingUtil.maskSensitiveData(message)
        
        // WorkManager를 통해 분석 작업 실행
        val inputData = Data.Builder()
            .putString("originalMessage", message)
            .putString("maskedMessage", maskedMessage)
            .putString("senderNumber", sender)
            .putLong("timestamp", System.currentTimeMillis())
            .build()
        
        val workRequest = OneTimeWorkRequestBuilder<SmsAnalysisWorker>()
            .setInputData(inputData)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 0, TimeUnit.SECONDS)
            .build()
        
        // Unique work로 중복 요청 방지
        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                "TEST_SMS_ANALYSIS",
                ExistingWorkPolicy.KEEP,
                workRequest
            )
        
        // 5초 후 플래그 리셋 (요청 완료 가정)
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            isRequesting = false
        }, 5000)
    }
    
    /**
     * 위험 메시지 테스트 예제 (백그라운드 분석 + 알림)
     */
    fun testDangerMessage(context: Context) {
        simulateSmsAnalysis(
            context = context,
            sender = "010-1234-5678",
            message = "[Web발신]\n" +
                    "안녕하세요, 고객님 \n" +
                    "평소 저희 이벤트에 관심 가져주신 분들께만 드리는 특별 안내입니다.\n" +
                    "\n" +
                    "이번 주말까지 진행되는 Casino 리워드 이벤트에 참여하시면\n" +
                    "입금 금액의 100%를 즉시 보너스로 지급 해드리며,\n" +
                    "추가로 최대 200만원 상당의 혜택을 받으실 수 있습니다.\n" +
                    "\n" +
                    "별도 앱 설치 없이, 아래 전용 링크로 접속 후\n" +
                    "간단히 이름과 연락처만 입력하시면 바로 참여 가능합니다.\n" +
                    "\uD83D\uDC49 https://casino.xyz\n" +
                    "\n" +
                    "본 문자는 이벤트 안내용이며, 타인에게 공유하지 마세요."
        )
    }
    
    /**
     * 경고 메시지 테스트 예제 (백그라운드 분석 + 알림)
     */
    fun testWarningMessage(context: Context) {
        simulateSmsAnalysis(
            context = context,
            sender = "1588-1234",
            message = "[KB국민카드] 고객님, 최근 결제 내역 확인을 위해 안내드립니다.\n" +
                    "\n" +
                    "일부 거래 정보가 정상적으로 확인되지 않아,\n" +
                    "보안을 위해 계좌 정보 및 승인 내역을 다시 한 번 확인해 주셔야 합니다.\n" +
                    "\n" +
                    "아래 확인 페이지로 접속하신 뒤,\n" +
                    "본인 인증 후 계좌번호와 최근 이용 내역을 점검해 주세요.\n" +
                    "\n" +
                    "▶ 계좌 확인 바로가기: https://secure-check.example.com\n" +
                    "\n" +
                    "※ 일정 시간 동안 확인이 되지 않을 경우,\n" +
                    "거래 보호를 위해 일부 서비스 이용이 제한될 수 있습니다.\n"
        )
    }
    
    /**
     * 정상 메시지 테스트 예제 (백그라운드 분석, 알림 없음)
     */
    fun testNormalMessage(context: Context) {
        simulateSmsAnalysis(
            context = context,
            sender = "1588-1234",
            message = "[KB국민카드]\n" +
                    "고객님께 맞춤형 교육 지원 프로그램을 안내드립니다.\n" +
                    "데이터 분석 관련 실무 역량을 쌓고 싶은 고객님들을 위해\n" +
                    "KB국민카드가 파트너 교육기관과 함께 준비한 정식 등록 과정입니다.\n" +
                    "\n" +
                    "교육비 부담을 줄이기 위해\n" +
                    "최대 170만원 지원 혜택과 과정 수료 시 인증 수료증 발급을 제공하며,\n" +
                    "일정 기준 충족 시 인턴십 연결 추천도 가능합니다.\n" +
                    "\n" +
                    "수업 안내, 커리큘럼, 신청 절차 등 상세 내용은 아래 링크를 통해 확인해주세요.\n" +
                    "▷ https://kb-card.com/edu"
        )
    }
    
}

