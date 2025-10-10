package com.example.iace2_frontend.ui.screens

import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
 */
@Composable
fun HomeScreen(
    isLoggedIn: Boolean,
    userName: String?,
    onLoginClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onAnalyzeMessage: (String) -> Unit
) {
    val context = LocalContext.current
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
                .offset(y = -100.dp),
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
                if (hasClipboardText) {
                    // 클립보드에 텍스트가 있을 때 - paste 아이콘
                    Icon(
                        painter = painterResource(id = R.drawable.paste),
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    // 클립보드가 비어있을 때 - forward 아이콘
                    Icon(
                        painter = painterResource(id = R.drawable.forward),
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                }
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

