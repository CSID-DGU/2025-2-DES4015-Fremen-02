package com.example.iace2_frontend

import android.Manifest
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.iace2_frontend.data.models.AnalysisResult
import com.example.iace2_frontend.data.models.RiskLevel
import com.example.iace2_frontend.ui.screens.AnalysisScreen
import com.example.iace2_frontend.ui.screens.HomeScreen
import com.example.iace2_frontend.ui.screens.LoginScreen
import com.example.iace2_frontend.ui.screens.NotificationDetailScreen
import com.example.iace2_frontend.ui.screens.NotificationListScreen
import com.example.iace2_frontend.ui.screens.SettingsScreen
import com.example.iace2_frontend.ui.screens.SplashScreen
import com.example.iace2_frontend.ui.theme.IACE2_FrontendTheme
import com.example.iace2_frontend.util.TokenManager
import com.example.iace2_frontend.util.NotificationUtil
import com.example.iace2_frontend.util.PermissionUtil
import com.example.iace2_frontend.util.RecentNotificationManager
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    
    // 권한 요청 런처
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // 권한 요청 결과 처리
        val allGranted = permissions.all { it.value }
        if (allGranted) {
            // 모든 권한이 허용됨
        } else {
            // 일부 권한이 거부됨
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        
        // TokenManager 초기화
        TokenManager.init(this)
        
        // 알림 채널 생성
        NotificationUtil.createNotificationChannel(this)
        
        // 권한 요청 (필요한 경우)
        requestPermissionsIfNeeded()
        
        setContent {
            IACE2_FrontendTheme {
                var showSplash by remember { mutableStateOf(true) }
                var showLogin by remember { mutableStateOf(false) }
                var showSettings by remember { mutableStateOf(false) }
                var showAnalysis by remember { mutableStateOf(false) }
                var showDetail by remember { mutableStateOf(false) }
                var showNotificationList by remember { mutableStateOf(false) }
                var messageToAnalyze by remember { mutableStateOf<String?>(null) }
                var detailData by remember { mutableStateOf<DetailData?>(null) }
                
                // 로그인 상태 관리
                var isLoggedIn by remember { mutableStateOf(false) }
                var userName by remember { mutableStateOf<String?>(null) }

                // 알림 클릭으로 앱이 열린 경우 처리
                LaunchedEffect(Unit) {
                    val showDetailFromIntent = intent.getBooleanExtra("showDetail", false)
                    val showAnalysisFromIntent = intent.getBooleanExtra("showAnalysis", false)
                    val messageFromIntent = intent.getStringExtra("message")
                    
                    if (showDetailFromIntent) {
                        // 상세 페이지로 이동 (서버 요청 없이)
                        val originalMessage = intent.getStringExtra("originalMessage") ?: ""
                        val senderNumber = intent.getStringExtra("senderNumber") ?: ""
                        val riskLevelStr = intent.getStringExtra("riskLevel") ?: "NORMAL"
                        val riskLevel = try {
                            RiskLevel.valueOf(riskLevelStr)
                        } catch (e: Exception) {
                            RiskLevel.NORMAL
                        }
                        val isDanger = intent.getBooleanExtra("isDanger", false)
                        val riskScoreValue = intent.getIntExtra("riskScore", -1)
                        val riskScore = if (riskScoreValue >= 0) riskScoreValue else null
                        val category = intent.getStringExtra("category")
                        val reason = intent.getStringExtra("reason")
                        val confidence = intent.getStringExtra("confidence")
                        
                        detailData = DetailData(
                            analysisResult = AnalysisResult(
                                riskLevel = riskLevel,
                                isDanger = isDanger,
                                riskScore = riskScore,
                                category = category,
                                reason = reason,
                                confidence = confidence
                            ),
                            originalMessage = originalMessage,
                            senderNumber = senderNumber
                        )
                        showDetail = true
                        showSplash = false
                    } else if (showAnalysisFromIntent && messageFromIntent != null) {
                        // 메시지 분석 화면으로 이동 (서버 요청)
                        messageToAnalyze = messageFromIntent
                        showAnalysis = true
                        showSplash = false
                    } else {
                        delay(1500)
                        showSplash = false
                    }
                }

                when {
                    showSplash -> SplashScreen()
                    showLogin -> LoginScreen(
                        onBackClick = { showLogin = false },
                        onLoginSuccess = { name ->
                            userName = name
                            isLoggedIn = true
                            showLogin = false
                        }
                    )
                    showSettings -> SettingsScreen(
                        onBackClick = { showSettings = false }
                    )
                    showNotificationList -> NotificationListScreen(
                        onBackClick = { showNotificationList = false },
                        onNotificationClick = { notification ->
                            detailData = DetailData(
                                analysisResult = notification.analysisResult,
                                originalMessage = notification.originalMessage,
                                senderNumber = notification.senderNumber
                            )
                            showNotificationList = false
                            showDetail = true
                        }
                    )
                    showDetail && detailData != null -> NotificationDetailScreen(
                        analysisResult = detailData!!.analysisResult,
                        originalMessage = detailData!!.originalMessage,
                        senderNumber = detailData!!.senderNumber,
                        onBackClick = {
                            showDetail = false
                            showNotificationList = true
                            detailData = null
                        }
                    )
                    showAnalysis && messageToAnalyze != null -> AnalysisScreen(
                        initialMessage = messageToAnalyze!!,
                        onBackClick = { 
                            showAnalysis = false
                            messageToAnalyze = null
                        }
                    )
                    else -> HomeScreen(
                        isLoggedIn = isLoggedIn,
                        userName = userName,
                        onLoginClick = { showLogin = true },
                        onSettingsClick = { showSettings = true },
                        onAnalyzeMessage = { message ->
                            messageToAnalyze = message
                            showAnalysis = true
                        },
                        onViewRecentNotification = {
                            // 알림 목록 화면으로 이동
                            showNotificationList = true
                        }
                    )
                }
            }
        }
    }
    
    /**
     * 상세 페이지 데이터
     */
    private data class DetailData(
        val analysisResult: AnalysisResult,
        val originalMessage: String,
        val senderNumber: String
    )
    
    /**
     * 필요한 권한 요청
     */
    private fun requestPermissionsIfNeeded() {
        val permissionsToRequest = mutableListOf<String>()
        
        // SMS 권한 확인
        if (!PermissionUtil.hasSmsPermission(this)) {
            permissionsToRequest.add(Manifest.permission.RECEIVE_SMS)
            permissionsToRequest.add(Manifest.permission.READ_SMS)
        }
        
        // 알림 권한 확인 (Android 13 이상)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!PermissionUtil.hasNotificationPermission(this)) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        
        // 권한 요청
        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }
}
