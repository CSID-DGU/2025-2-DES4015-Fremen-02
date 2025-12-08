package com.example.iace2_frontend.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
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
 * 스플래시 화면
 * 
 * 앱 실행 시 1.5초 동안 보여지는 초기 화면
 * - 로고와 앱 이름을 표시
 */
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

@Preview(showBackground = true, name = "스플래시 화면")
@Composable
fun SplashScreenPreview() {
    IACE2_FrontendTheme {
        SplashScreen()
    }
}

