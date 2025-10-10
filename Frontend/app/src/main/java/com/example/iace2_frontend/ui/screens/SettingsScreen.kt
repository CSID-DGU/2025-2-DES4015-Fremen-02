package com.example.iace2_frontend.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
 * 설정 화면
 * 
 * - 기본 정보
 * - 알림 설정
 * - 로그아웃
 * - 회원 탈퇴
 * 
 * @param onBackClick 뒤로가기 버튼 클릭 시 콜백
 */
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

/**
 * 설정 메뉴 아이템
 * 
 * @param icon 아이콘 리소스 ID
 * @param title 메뉴 제목
 * @param iconTint 아이콘 색상
 * @param textColor 텍스트 색상
 * @param onClick 클릭 시 콜백
 */
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

@Preview(showBackground = true, name = "설정 화면")
@Composable
fun SettingsScreenPreview() {
    IACE2_FrontendTheme {
        SettingsScreen(onBackClick = {})
    }
}

