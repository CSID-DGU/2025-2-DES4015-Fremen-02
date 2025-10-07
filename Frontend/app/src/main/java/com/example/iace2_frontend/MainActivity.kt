package com.example.iace2_frontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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

                LaunchedEffect(Unit) {
                    delay(1500)
                    showSplash = false
                }

                when {
                    showSplash -> SplashScreen()
                    showLogin -> LoginScreen(
                        onBackClick = { showLogin = false }
                    )
                    showSettings -> SettingsScreen(
                        onBackClick = { showSettings = false }
                    )
                    else -> HomeScreen(
                        onLoginClick = { showLogin = true },
                        onSettingsClick = { showSettings = true }
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
fun HomeScreen(onLoginClick: () -> Unit, onSettingsClick: () -> Unit) {
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
                TextButton(onClick = onLoginClick) {
                    Text(
                        text = "Login",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
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

        // 중앙 컨텐츠
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart) // 왼쪽 위 정렬
                .padding(start = 60.dp, top = 140.dp), // 화면 위쪽, 왼쪽 여백
            horizontalAlignment = Alignment.Start // 내부 요소는 왼쪽 정렬
        ) {
            // Paste 아이콘
            Image(
                painter = painterResource(id = R.drawable.paste),
                contentDescription = "Paste",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(38.dp)
                    .offset(x=(-16).dp)
            )

            Spacer(modifier = Modifier.height(0.dp))

            // “붙여넣을 수 있는...” 텍스트
            Text(
                text = "붙여넣을 수 있는\n메시지가 없어요",
                fontSize = 18.sp,
                lineHeight = 27.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                textAlign = TextAlign.Start // 왼쪽 정렬 유지
            )

            Spacer(modifier = Modifier.height(13.dp)) // 아래 회색 박스와 간격

            // 경고 텍스트 박스
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .background(
                        color = Color(0xFF1E1E1E),
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

        // "메세지함 바로가기" 버튼 - 전체 화면 기준 중앙 배치
        Button(
            onClick = { /* TODO */ },
            modifier = Modifier
                .width(175.dp)
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
                    text = "메세지함 바로가기",
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
fun LoginScreen(onBackClick: () -> Unit) {
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
                onClick = { /* TODO: 카카오 로그인 */ },
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

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    IACE2_FrontendTheme {
        HomeScreen(onLoginClick = {}, onSettingsClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    IACE2_FrontendTheme {
        LoginScreen(onBackClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    IACE2_FrontendTheme {
        SettingsScreen(onBackClick = {})
    }
}
