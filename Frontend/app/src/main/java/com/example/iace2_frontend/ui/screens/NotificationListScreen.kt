package com.example.iace2_frontend.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.iace2_frontend.R
import com.example.iace2_frontend.data.models.RiskLevel
import com.example.iace2_frontend.util.RecentNotificationManager
import java.text.SimpleDateFormat
import java.util.*

/**
 * 알림 목록 화면
 * 
 * 날짜/시간별로 정렬된 알림 목록을 표시합니다.
 * 
 * @param onBackClick 뒤로가기 버튼 클릭 시 콜백
 * @param onNotificationClick 알림 클릭 시 콜백 (상세 화면으로 이동)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationListScreen(
    onBackClick: () -> Unit,
    onNotificationClick: (com.example.iace2_frontend.util.RecentNotificationData) -> Unit
) {
    val context = LocalContext.current
    var notifications by remember { mutableStateOf<List<com.example.iace2_frontend.util.RecentNotificationData>>(emptyList()) }
    
    // 알림 목록 불러오기
    LaunchedEffect(Unit) {
        notifications = RecentNotificationManager.getAllNotifications(context)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("알림 목록") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Image(
                            painter = painterResource(id = R.drawable.back),
                            contentDescription = "뒤로가기",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A1A1A),
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFF1A1A1A)
    ) { paddingValues ->
        if (notifications.isEmpty()) {
            // 알림이 없을 때
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "알림이 없습니다",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                    )
                    Text(
                        text = "위험 또는 경고 문자가 감지되면\n여기에 표시됩니다",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }
            }
        } else {
            // 알림 목록 표시
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(notifications) { notification ->
                    NotificationListItem(
                        notification = notification,
                        onClick = { onNotificationClick(notification) }
                    )
                }
            }
        }
    }
}

/**
 * 알림 목록 아이템
 */
@Composable
private fun NotificationListItem(
    notification: com.example.iace2_frontend.util.RecentNotificationData,
    onClick: () -> Unit
) {
    val riskLevel = notification.analysisResult.riskLevel
    val cardColor = when (riskLevel) {
        RiskLevel.DANGER -> Color(0xFFFF6B6B).copy(alpha = 0.1f)
        RiskLevel.WARN -> Color(0xFFFFA726).copy(alpha = 0.1f)
        RiskLevel.NORMAL -> Color(0xFF4A9FF5).copy(alpha = 0.1f)
    }
    
    val borderColor = when (riskLevel) {
        RiskLevel.DANGER -> Color(0xFFFF6B6B)
        RiskLevel.WARN -> Color(0xFFFFA726)
        RiskLevel.NORMAL -> Color(0xFF4A9FF5)
    }
    
    val levelText = when (riskLevel) {
        RiskLevel.DANGER -> "위험"
        RiskLevel.WARN -> "경고"
        RiskLevel.NORMAL -> "안전"
    }
    
    val levelTextColor = when (riskLevel) {
        RiskLevel.DANGER -> Color(0xFFFF6B6B)
        RiskLevel.WARN -> Color(0xFFFFA726)
        RiskLevel.NORMAL -> Color(0xFF4A9FF5)
    }
    
    // 날짜/시간 포맷팅
    val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일 HH:mm", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(notification.timestamp))
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 상단: 위험 레벨과 날짜/시간
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 위험 레벨 배지
                Box(
                    modifier = Modifier
                        .background(
                            color = cardColor,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = levelText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = levelTextColor
                    )
                }
                
                // 날짜/시간
                Text(
                    text = formattedDate,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            
            // 발신자
            Text(
                text = "발신자: ${notification.senderNumber}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            
            // 메시지 미리보기 (최대 2줄)
            Text(
                text = notification.originalMessage,
                fontSize = 13.sp,
                color = Color(0xFFCCCCCC),
                maxLines = 2,
                modifier = Modifier.fillMaxWidth()
            )
            
            // 분석 결과 미리보기
            notification.analysisResult.reason?.let { reason ->
                Text(
                    text = reason,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

