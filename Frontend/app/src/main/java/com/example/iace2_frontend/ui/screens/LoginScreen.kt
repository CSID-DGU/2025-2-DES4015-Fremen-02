package com.example.iace2_frontend.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.iace2_frontend.R
import com.example.iace2_frontend.ui.theme.IACE2_FrontendTheme

/**
 * 로그인 화면
 * 
 * 카카오, 구글, 네이버 소셜 로그인 버튼 제공
 * 
 * @param onBackClick 뒤로가기 버튼 클릭 시 콜백
 * @param onLoginSuccess 로그인 성공 시 콜백 (사용자 이름 전달)
 */
@Composable
fun LoginScreen(
    onBackClick: () -> Unit,
    onLoginSuccess: (String) -> Unit // 콜백 매개변수
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
                    onLoginSuccess("조효동님") // 버튼 클릭 시 콜백 호풀
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

@Preview(showBackground = true, name = "로그인 화면")
@Composable
fun LoginScreenPreview() {
    IACE2_FrontendTheme {
        LoginScreen(onBackClick = {}, onLoginSuccess = {})
    }
}

