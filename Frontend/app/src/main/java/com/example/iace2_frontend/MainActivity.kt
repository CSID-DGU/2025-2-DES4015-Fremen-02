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

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize() // 화면 크기
            .background(Color(0xFF1E1E1E)) // 다크 배경
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 254.dp, bottom = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Logo image - 상단에 배치
            Image(
                painter = painterResource(id = R.drawable.sentinel_logo),
                contentDescription = "Sentinel Logo",
                modifier = Modifier.size(223.dp)
            )

            // Text - 하단에 배치
            Text(
                text = "sentinel",
                fontSize = 27.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                letterSpacing = 0.sp,
                lineHeight = (27 * 1.5).sp
            )
        }
    }
}

@Composable
fun HomeScreen(
    isLoggedIn: Boolean, // 로그인 상태 여부
    userName: String?, // 로그인된 유저 이름
    onLoginClick: () -> Unit,  // 로그인 버튼 눌렀을 시 동작
    onSettingsClick: () -> Unit, // 설정 버튼 눌렀을 시 동작
    onAnalyzeMessage: (String) -> Unit // 클립보드 텍스트를 분석할 때 호출
) {
    val context = LocalContext.current // 현재 안드로이드 context가져옴
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    
    // 클립보드에 텍스트가 있는지 확인
    val hasClipboardText = clipboardManager.hasPrimaryClip() && 
                          clipboardManager.primaryClipDescription?.hasMimeType("text/plain") == true
    
    // 클립보드에서 텍스트 가져오기 함수
    fun pasteFromClipboard() {
        clipboardManager.primaryClip?.let { clip ->
            if (clip.itemCount > 0) {
                val text = clip.getItemAt(0).text?.toString()
                if (!text.isNullOrEmpty()) {
                    onAnalyzeMessage(text)
                }
            }
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E))
    ) {
        // 상단 바
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 왼쪽 로고
            Image(
                painter = painterResource(id = R.drawable.sentinel_logo),
                contentDescription = "Logo",
                modifier = Modifier.size(40.dp)
            )
            
            // 오른쪽 버튼들
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isLoggedIn && userName != null) {
                    // 로그인 상태 - 사용자 이름 표시
                    Text(
                        text = userName,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    // 비로그인 상태 - Login 버튼
                    TextButton(onClick = onLoginClick) {
                        Text(
                            text = "Login",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                }
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.settings),
                        contentDescription = "Settings",
                        tint = Color.Gray
                    )
                }
            }
        }

        // 중앙 컨텐츠 - 클립보드 상태에 따라 다르게 표시
        if (hasClipboardText) {
            // 클립보드에 텍스트가 있을 때
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopStart)
                    .padding(start = 60.dp, top = 140.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // Doubt 아이콘
                Image(
                    painter = painterResource(id = R.drawable.doubt),
                    contentDescription = "Doubt",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(38.dp)
                        .offset(x = (-16).dp)
                )

                Spacer(modifier = Modifier.height(0.dp))

                // "의심스러운 문자를..." 텍스트
                Text(
                    text = "의심스러운 문자를\n받으셨나요?",
                    fontSize = 18.sp,
                    lineHeight = 27.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(13.dp))

                // 경고 텍스트 박스
                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .background(
                            color = Color(0xFF2A2A2A),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "메세지 붙여넣기 시 민감한 개인정보가\n포함되지 않도록 유의해주세요.",
                        fontSize = 11.sp,
                        lineHeight = 16.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        } else {
            // 클립보드에 텍스트가 없을 때
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopStart)
                    .padding(start = 60.dp, top = 140.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // Paste 아이콘
                Image(
                    painter = painterResource(id = R.drawable.paste),
                    contentDescription = "Paste",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(38.dp)
                        .offset(x = (-16).dp)
                )

                Spacer(modifier = Modifier.height(0.dp))

                // "붙여넣을 수 있는..." 텍스트
                Text(
                    text = "붙여넣을 수 있는\n메시지가 없어요",
                    fontSize = 18.sp,
                    lineHeight = 27.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(13.dp))

                // 경고 텍스트 박스
                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .background(
                            color = Color(0xFF2A2A2A),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "메세지에 포함된 링크는 절대 클릭하지 말고,\n복사 시 잘못 누르지 않도록 유의해주세요.",
                        fontSize = 11.sp,
                        lineHeight = 16.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }

        // 버튼 - 클립보드 상태에 따라 다르게 표시
        Button(
            onClick = {
                if (hasClipboardText) {
                    pasteFromClipboard()
                } else {
                    // TODO: 메세지함으로 이동
                }
            },
            modifier = Modifier
                .width(if (hasClipboardText) 150.dp else 175.dp)
                .height(38.dp)
                .align(Alignment.Center)
                .offset(y = -100.dp), // 중앙에서 위치조절
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4A9FF5)
            ),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.forward),
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (hasClipboardText) "메세지 붙여넣기" else "메세지함 바로가기",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
            }
        }

        // 하단 네비게이션 바
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color(0xFF1A1A1A))
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.friend),
                    contentDescription = "친구",
                    tint = Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    text = "친구",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.home),
                    contentDescription = "홈",
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    text = "홈",
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
            
    Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.menu),
                    contentDescription = "메뉴",
                    tint = Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    text = "메뉴",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun LoginScreen(
    onBackClick: () -> Unit,
    onLoginSuccess: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E))
    ) {
        // 상단 바 (Back 버튼 + 로그인 제목)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back 버튼
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(id = R.drawable.back),
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            // 로그인 텍스트 (중앙)
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "로그인",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            
            // 오른쪽 공간 균형 (Back 버튼과 대칭)
            Spacer(modifier = Modifier.size(48.dp))
        }
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = 120.dp),
        horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 상단 sentinel 텍스트
            Text(
                text = "sentinel",
                fontSize = 32.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(60.dp))
            
            // 로고
            Image(
                painter = painterResource(id = R.drawable.sentinel_logo),
                contentDescription = "Sentinel Logo",
                modifier = Modifier.size(200.dp)
            )
        }
        
        // 로그인 버튼들 - 하단에 배치
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp, vertical = 60.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 카카오 로그인
            Button(
                onClick = { 
                    // TODO: 실제 카카오 로그인 API 연동
                    // 현재는 더미 데이터로 로그인
                    onLoginSuccess("조효동님")
                },
                modifier = Modifier
                    .width(280.dp)
                    .height(50.dp)
                    .align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFEE500)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.icon_kakao),
                        contentDescription = "Kakao Icon",
                        modifier = Modifier.size(20.dp),
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "카카오로 로그인하기",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                }
            }
            
            // 구글 로그인
            Button(
                onClick = { /* TODO: 구글 로그인 */ },
                modifier = Modifier
                    .width(280.dp)
                    .height(50.dp)
                    .align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.icon_google),
                        contentDescription = "Google Icon",
                        modifier = Modifier.size(20.dp),
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "구글로 로그인하기",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray
                    )
                }
            }
            
            // 네이버 로그인
            Button(
                onClick = { /* TODO: 네이버 로그인 */ },
                modifier = Modifier
                    .width(280.dp)
                    .height(50.dp)
                    .align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF03C75A)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.icon_naver),
                        contentDescription = "Naver Icon",
                        modifier = Modifier.size(20.dp),
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "네이버로 로그인하기",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E))
    ) {
        // 상단 바
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back 버튼
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(id = R.drawable.back),
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            // 설정 텍스트 (중앙)
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "설정",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            
            // 오른쪽 공간 균형
            Spacer(modifier = Modifier.size(48.dp))
        }
        
        // 설정 메뉴 목록
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 80.dp)
        ) {
            // 기본 정보
            SettingsMenuItem(
                icon = R.drawable.account_circle,
                title = "기본 정보",
                iconTint = Color.White,
                onClick = { /* TODO */ }
            )
            
            HorizontalDivider(
                color = Color(0xFF2A2A2A),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            // 알림 설정
            SettingsMenuItem(
                icon = R.drawable.notifications,
                title = "알림 설정",
                iconTint = Color.White,
                onClick = { /* TODO */ }
            )
            
            HorizontalDivider(
                color = Color(0xFF2A2A2A),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            // 로그아웃
            SettingsMenuItem(
                icon = R.drawable.send,
                title = "로그아웃",
                iconTint = Color(0xFFFFB74D),
                textColor = Color(0xFFF7B526),
                onClick = { /* TODO */ }
            )
            
            HorizontalDivider(
                color = Color(0xFF2A2A2A),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            // 회원 탈퇴
            SettingsMenuItem(
                icon = R.drawable.phone_link,
                title = "회원 탈퇴",
                iconTint = Color(0xFFFF6B9D),
                textColor = Color(0xFFF35686),
                onClick = { /* TODO */ }
            )
        }
    }
}

@Composable
fun SettingsMenuItem(
    icon: Int,
    title: String,
    iconTint: Color,
    textColor: Color = Color.White,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 왼쪽 아이콘
        Icon(
            painter = painterResource(id = icon),
            contentDescription = title,
            tint = iconTint,
            modifier = Modifier.size(28.dp)
        )
        
        Spacer(modifier = Modifier.width(20.dp))
        
        // 제목
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = textColor,
            modifier = Modifier.weight(1f)
        )
        
        // 오른쪽 화살표
        Icon(
            painter = painterResource(id = R.drawable.next),
            contentDescription = "Next",
            tint = Color.Gray,
            modifier = Modifier.size(20.dp)
        )
    }
}

// 분석 결과 데이터 모델
data class AnalysisResult(
    val isSmishing: Boolean,
    val confidence: String,
    val sender: String?,
    val content: String?,
    val links: String?,
    val category: String?
)

@Composable
fun AnalysisScreen(message: String, onBackClick: () -> Unit) {
    var isAnalyzing by remember { mutableStateOf(true) }
    var analysisResult by remember { mutableStateOf<AnalysisResult?>(null) }
    
    // 분석 시뮬레이션 (실제로는 서버 API 호출)
    LaunchedEffect(message) {
        delay(2000) // 2초 로딩 시뮬레이션
        // TODO: 실제로는 서버 API 호출
        analysisResult = AnalysisResult(
            isSmishing = false,
            confidence = "높아요",
            sender = "KB국민카드 공식 번호 확인됨",
            content = "개인정보 요구-위험 문구 없음",
            links = "KB국민카드 공식 도메인 검증 완료",
            category = "합법적인 광고/홍보성 문자로 분류되었습니다."
        )
        isAnalyzing = false
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E))
    ) {
        // 상단 바
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(id = R.drawable.back),
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "메시지 분석",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            
            Spacer(modifier = Modifier.size(48.dp))
        }
        
        // 메인 콘텐츠 - 스크롤 가능
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 80.dp, start = 20.dp, end = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // 메시지 말풍선
            MessageBubble(message = message)
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // 분석 중 또는 결과 표시
            if (isAnalyzing) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF4A9FF5),
                            modifier = Modifier.size(50.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "메시지 분석 중...",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                }
            } else {
                analysisResult?.let     { result ->
                    AnalysisResultCard(result = result)
                }
            }
            
        Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun MessageBubble(message: String) {
    // 사용자가 보낸 메시지 - 오른쪽 정렬
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.End
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = Color(0xFF4A9FF5),
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = 16.dp,
                        bottomEnd = 4.dp
                    )
                )
                .padding(16.dp)
                .widthIn(max = 280.dp)
        ) {
            Text(
                text = message,
                color = Color.White,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun AnalysisResultCard(result: AnalysisResult) {
    // 시스템 응답 - 왼쪽 정렬
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 320.dp) // 카드 최대 폭 제한
                .background( // 카드 배경 색 + 모서리 둥글게
                    color = Color(0xFF2A2A2A),
                    shape = RoundedCornerShape(
                        topStart = 4.dp,
                        topEnd = 16.dp,
                        bottomStart = 16.dp,
                        bottomEnd = 16.dp
                    )
                )
                .padding(20.dp) // 카드 내부의 여백 설정
        ) {
            // 체크 아이콘과 결과 텍스트
            Row(
                verticalAlignment = Alignment.CenterVertically, // 아이콘와 텍스트를 세로로 가운데 맞추기
                modifier = Modifier.padding(bottom = 16.dp) // 아래쪽 여백을 두어 다음 텍스트와 간격 확보
            ) {
                Icon(
                    painter = painterResource(id = if (result.isSmishing) R.drawable.forward else R.drawable.forward),
                    contentDescription = null,
                    tint = if (result.isSmishing) Color(0xFFFF6B6B) else Color(0xFF4A9FF5),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = if (result.isSmishing) "위험" else "정상",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (result.isSmishing) Color(0xFFFF6B6B) else Color(0xFF4A9FF5)
                )
            }
            
            // 메인 메시지
            Text(
                text = if (result.isSmishing) 
                    "스미싱일 가능성이 ${result.confidence}" 
                else 
                    "스미싱이 아닐 가능성이 ${result.confidence}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = if (result.isSmishing) 
                    "이 문자는 스미싱입니다." 
                else 
                    "이 문자는 스미싱이 아닙니다.",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 20.dp)
            )
            
            // 세부 정보
            result.sender?.let {
                DetailRow(label = "발신자:", value = it)
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            result.content?.let {
                DetailRow(label = "내용:", value = it)
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            result.links?.let {
                DetailRow(label = "링크:", value = it)
            }
            
            // 분류 결과
            result.category?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.forward),
                        contentDescription = null,
                        tint = Color(0xFF4A9FF5),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = it,
                        fontSize = 13.sp,
                        color = Color.White,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Column {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 14.sp,
            color = Color.White,
            lineHeight = 20.sp
        )
    }
}

// ============== Preview 모음 ==============

@Preview(showBackground = true, name = "스플래시 화면")
@Composable
fun SplashScreenPreview() {
    IACE2_FrontendTheme {
        SplashScreen()
    }
}

@Preview(showBackground = true, name = "홈 화면 - 비로그인")
@Composable
fun HomeScreenPreview() {
    IACE2_FrontendTheme {
        HomeScreen(
            isLoggedIn = false,
            userName = null,
            onLoginClick = {},
            onSettingsClick = {},
            onAnalyzeMessage = {}
        )
    }
}

@Preview(showBackground = true, name = "홈 화면 - 로그인")
@Composable
fun HomeScreenLoggedInPreview() {
    IACE2_FrontendTheme {
        HomeScreen(
            isLoggedIn = true,
            userName = "조효동님",
            onLoginClick = {},
            onSettingsClick = {},
            onAnalyzeMessage = {}
        )
    }
}

@Preview(showBackground = true, name = "로그인 화면")
@Composable
fun LoginScreenPreview() {
    IACE2_FrontendTheme {
        LoginScreen(onBackClick = {}, onLoginSuccess = {})
    }
}

@Preview(showBackground = true, name = "설정 화면")
@Composable
fun SettingsScreenPreview() {
    IACE2_FrontendTheme {
        SettingsScreen(onBackClick = {})
    }
}

@Preview(showBackground = true, name = "분석 화면 - 정상")
@Composable
fun AnalysisScreenPreview() {
    IACE2_FrontendTheme {
        AnalysisScreen(
            message = "[Web발신]\n(광고)[KB국민카드] 데이터 분석 부트캠프\n\n최대 170만원 지원금과 인턴십 기회까지, 데이터 분석 부트캠프",
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true, name = "메시지 말풍선 - 사용자")
@Composable
fun MessageBubblePreview() {
    IACE2_FrontendTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E1E1E))
                .padding(16.dp)
        ) {
            MessageBubble(
                message = "[Web발신]\n(광고)[KB국민카드] 데이터 분석 부트캠프\n\n최대 170만원 지원금과 인턴십 기회까지"
            )
        }
    }
}

@Preview(showBackground = true, name = "분석 결과 카드 - 정상")
@Composable
fun AnalysisResultCardPreview() {
    IACE2_FrontendTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E1E1E))
                .padding(16.dp)
        ) {
            AnalysisResultCard(
                result = AnalysisResult(
                    isSmishing = false,
                    confidence = "높아요",
                    sender = "KB국민카드 공식 번호 확인됨",
                    content = "개인정보 요구-위험 문구 없음",
                    links = "KB국민카드 공식 도메인 검증 완료",
                    category = "합법적인 광고/홍보성 문자로 분류되었습니다."
                )
            )
        }
    }
}

@Preview(showBackground = true, name = "분석 결과 카드 - 위험")
@Composable
fun AnalysisResultCardDangerPreview() {
    IACE2_FrontendTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E1E1E))
                .padding(16.dp)
        ) {
            AnalysisResultCard(
                result = AnalysisResult(
                    isSmishing = true,
                    confidence = "매우 높아요",
                    sender = "미등록 번호로 확인됨",
                    content = "개인정보 요구-위험 문구 포함",
                    links = "의심스러운 도메인 감지",
                    category = "스미싱 문자로 분류되었습니다. 절대 링크를 클릭하지 마세요!"
                )
            )
        }
    }
}

@Preview(showBackground = true, name = "설정 메뉴 아이템")
@Composable
fun SettingsMenuItemPreview() {
    IACE2_FrontendTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E1E1E))
        ) {
            Column {
                SettingsMenuItem(
                    icon = R.drawable.account_circle,
                    title = "기본 정보",
                    iconTint = Color.White,
                    onClick = {}
                )
                SettingsMenuItem(
                    icon = R.drawable.send,
                    title = "로그아웃",
                    iconTint = Color(0xFFFFB74D),
                    textColor = Color(0xFFF7B526),
                    onClick = {}
                )
            }
        }
    }
}
