package com.example.iace2_frontend

import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import com.example.iace2_frontend.ui.screens.AnalysisScreen
import com.example.iace2_frontend.ui.screens.HomeScreen
import com.example.iace2_frontend.ui.screens.LoginScreen
import com.example.iace2_frontend.ui.screens.SettingsScreen
import com.example.iace2_frontend.ui.screens.SplashScreen
import com.example.iace2_frontend.ui.theme.IACE2_FrontendTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            IACE2_FrontendTheme {
                var showSplash by remember { mutableStateOf(true) }
                var showLogin by remember { mutableStateOf(false) }
                var showSettings by remember { mutableStateOf(false) }
                var showAnalysis by remember { mutableStateOf(false) }
                var messageToAnalyze by remember { mutableStateOf<String?>(null) }
                
                // 로그인 상태 관리
                var isLoggedIn by remember { mutableStateOf(false) }
                var userName by remember { mutableStateOf<String?>(null) }

                LaunchedEffect(Unit) {
                    delay(1500)
                    showSplash = false
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
                    showAnalysis && messageToAnalyze != null -> AnalysisScreen(
                        message = messageToAnalyze!!,
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
                        }
                    )
                }
            }
        }
    }
}
