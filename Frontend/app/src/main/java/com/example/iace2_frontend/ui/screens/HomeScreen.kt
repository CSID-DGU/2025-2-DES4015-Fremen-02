package com.example.iace2_frontend.ui.screens

import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.iace2_frontend.R
import com.example.iace2_frontend.ui.theme.IACE2_FrontendTheme
import com.example.iace2_frontend.util.RecentNotificationManager
import com.example.iace2_frontend.util.TestUtil

/**
 * 홈 화면
 * 
 * - 클립보드 상태에 따라 다른 UI 표시
 * - 로그인 상태에 따라 사용자 이름 또는 Login 버튼 표시
 * - 하단 네비게이션 바 포함
 * 
 * @param isLoggedIn 로그인 상태 여부
 * @param userName 로그인된 유저 이름
 * @param onLoginClick 로그인 버튼 클릭 시 콜백
 * @param onSettingsClick 설정 버튼 클릭 시 콜백
 * @param onAnalyzeMessage 클립보드 텍스트 분석 시 콜백
 * @param onViewRecentNotification 최근 알림 보기 클릭 시 콜백
 */
@Composable
fun HomeScreen(
    isLoggedIn: Boolean,
    userName: String?,
    onLoginClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onAnalyzeMessage: (String) -> Unit,
    onViewRecentNotification: () -> Unit = {}
) {
    val context = LocalContext.current
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val coroutineScope = rememberCoroutineScope()
    
    // 문자수신 메뉴 상태 (메인 Box에서 관리)
    var showSmsMenu by remember { mutableStateOf(false) }
    var isRequesting by remember { mutableStateOf(false) }
    
    // 클립보드에 텍스트가 있는지 확인
    val hasClipboardText = clipboardManager.hasPrimaryClip() && 
                          clipboardManager.primaryClipDescription?.hasMimeType("text/plain") == true
    
    // 최근 알림이 있는지 확인
    val hasRecentNotification = remember { 
        mutableStateOf(RecentNotificationManager.hasRecentNotification(context)) 
    }
    
    // 최근 알림 상태 업데이트
    LaunchedEffect(Unit) {
        hasRecentNotification.value = RecentNotificationManager.hasRecentNotification(context)
    }
    
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
                
                // 문자수신 버튼 (로컬 테스트용)
                TextButton(
                    onClick = { if (!isRequesting) showSmsMenu = !showSmsMenu },
                    enabled = !isRequesting
                ) {
                    Text(
                        text = if (isRequesting) "분석중" else "문자수신",
                        color = if (isRequesting) Color.Gray else Color(0xFF4A9FF5),
                        fontSize = 10.sp
                    )
                }
            }
        }
        
        // 드롭다운 메뉴 오버레이 (메뉴가 열려있을 때만 표시)
        if (showSmsMenu && !isRequesting) {
            // 배경 클릭 시 메뉴 닫기 (투명)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent)
                    .clickable { showSmsMenu = false }
            )
            
            // 드롭다운 메뉴 (오른쪽 상단에 고정)
            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-16).dp, y = 56.dp)
                    .background(
                        color = Color(0xFF2A2A2A),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp)
                    .width(120.dp)
            ) {
                TextButton(
                    onClick = {
                        if (!isRequesting) {
                            isRequesting = true
                            showSmsMenu = false
                            TestUtil.testDangerMessage(context)
                            // 5초 후 플래그 리셋
                            coroutineScope.launch {
                                delay(5000)
                                isRequesting = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("위험 문자", color = Color(0xFFFF6B6B), fontSize = 12.sp)
                }
                TextButton(
                    onClick = {
                        if (!isRequesting) {
                            isRequesting = true
                            showSmsMenu = false
                            TestUtil.testWarningMessage(context)
                            // 5초 후 플래그 리셋
                            coroutineScope.launch {
                                delay(5000)
                                isRequesting = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("경고 문자", color = Color(0xFFFFA726), fontSize = 12.sp)
                }
                TextButton(
                    onClick = {
                        if (!isRequesting) {
                            isRequesting = true
                            showSmsMenu = false
                            TestUtil.testNormalMessage(context)
                            // 5초 후 플래그 리셋
                            coroutineScope.launch {
                                delay(5000)
                                isRequesting = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("안전 문자", color = Color(0xFF4A9FF5), fontSize = 12.sp)
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

        // 버튼들 - 세로로 배치 (일정한 간격 유지)
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = -60.dp), // 버튼을 아래로 이동
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp) // 간격을 12dp에서 16dp로 증가
        ) {
            // 메시지 붙여넣기 버튼 (항상 표시, 분석 화면으로 이동)
            Button(
                onClick = {
                    if (hasClipboardText) {
                        // 클립보드에 텍스트가 있으면 바로 분석
                        pasteFromClipboard()
                    } else {
                        // 클립보드에 텍스트가 없어도 분석 화면으로 이동 (빈 메시지로)
                        onAnalyzeMessage("")
                    }
                },
                modifier = Modifier
                    .width(150.dp)
                    .height(38.dp),
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
                        painter = painterResource(id = R.drawable.paste),
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "메세지 붙여넣기",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                }
            }

            // 최근 알림 보기 버튼 (항상 표시)
            Button(
                onClick = onViewRecentNotification,
                modifier = Modifier
                    .width(175.dp)
                    .height(38.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2A2A2A)
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.notifications),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "최근 알림 보기",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
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

// Preview
@Preview(showBackground = true, name = "홈 화면 - 비로그인")
@Composable
fun HomeScreenPreview() {
    IACE2_FrontendTheme {
        HomeScreen(
            isLoggedIn = false,
            userName = null,
            onLoginClick = {},
            onSettingsClick = {},
            onAnalyzeMessage = {},
            onViewRecentNotification = {}
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
            onAnalyzeMessage = {},
            onViewRecentNotification = {}
        )
    }
}

