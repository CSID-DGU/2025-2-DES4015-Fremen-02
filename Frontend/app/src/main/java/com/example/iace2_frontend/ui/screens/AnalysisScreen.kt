package com.example.iace2_frontend.ui.screens

import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.iace2_frontend.R
import com.example.iace2_frontend.data.models.AnalysisResult
import com.example.iace2_frontend.data.models.MessageAnalysis
import com.example.iace2_frontend.data.models.RiskLevel
import com.example.iace2_frontend.ui.theme.IACE2_FrontendTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 메시지 분석 화면 (연속 분석 가능)
 * 
 * 사용자가 여러 메시지를 연속으로 붙여넣어 분석할 수 있는 화면
 * 
 * @param initialMessage 처음 전달된 메시지
 * @param onBackClick 뒤로가기 버튼 클릭 시 콜백
 */
@Composable
fun AnalysisScreen(initialMessage: String, onBackClick: () -> Unit) {
    val context = LocalContext.current
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    
    // 메시지 리스트 관리
    var messageList by remember { 
        mutableStateOf(
            listOf(
                MessageAnalysis(
                    id = System.currentTimeMillis(),
                    message = initialMessage,
                    isAnalyzing = true
                )
            )
        )
    }
    
    // 초기 메시지 분석
    LaunchedEffect(Unit) {
        delay(2000)
        messageList = messageList.map { item ->
            if (item.id == messageList.first().id) {
                item.copy(
                    isAnalyzing = false,
                    result = AnalysisResult(
                        riskLevel = RiskLevel.NORMAL, // 정상
                        isSmishing = false,
                        confidence = "높아요",
                        sender = "KB국민카드 공식 번호 확인됨",
                        content = "개인정보 요구-위험 문구 없음",
                        links = "KB국민카드 공식 도메인 검증 완료",
                        category = "합법적인 광고/홍보성 문자로 분류되었습니다."
                    )
                )
            } else item
        }
    }
    
    // 새 메시지 분석 함수
    fun analyzeNewMessage(message: String) {
        val newMessage = MessageAnalysis(
            id = System.currentTimeMillis(),
            message = message,
            isAnalyzing = true
        )
        messageList = messageList + newMessage
        
        // 자동 스크롤
        coroutineScope.launch {
            delay(100)
            listState.animateScrollToItem(messageList.size - 1)
        }
        
        // 분석 시뮬레이션 (랜덤으로 다양한 결과 생성)
        coroutineScope.launch {
            delay(2000)
            val randomResult = when ((0..2).random()) {
                0 -> AnalysisResult(
                    riskLevel = RiskLevel.NORMAL,
                    isSmishing = false,
                    confidence = "높아요",
                    sender = "공식 번호 확인됨",
                    content = "개인정보 요구-위험 문구 없음",
                    links = "공식 도메인 검증 완료",
                    category = "합법적인 광고/홍보성 문자로 분류되었습니다."
                )
                1 -> AnalysisResult(
                    riskLevel = RiskLevel.WARN,
                    isSmishing = false,
                    confidence = "보통",
                    sender = "번호 정보 확인 필요",
                    content = "일부 의심스러운 문구 포함",
                    links = "링크 주의 필요",
                    category = "주의가 필요한 메시지입니다. 링크 클릭에 유의하세요."
                )
                else -> AnalysisResult(
                    riskLevel = RiskLevel.DANGER,
                    isSmishing = true,
                    confidence = "매우 높아요",
                    sender = "미확인 발신번호",
                    content = "개인정보 및 금융정보 요구 확인",
                    links = "의심스러운 도메인 감지",
                    category = "스미싱 위험 문자로 분류되었습니다. 절대 링크를 클릭하지 마세요!"
                )
            }
            
            messageList = messageList.map { item ->
                if (item.id == newMessage.id) {
                    item.copy(
                        isAnalyzing = false,
                        result = randomResult
                    )
                } else item
            }
        }
    }
    
    // 클립보드에서 붙여넣기
    fun pasteFromClipboard() {
        clipboardManager.primaryClip?.let { clip ->
            if (clip.itemCount > 0) {
                val text = clip.getItemAt(0).text?.toString()
                if (!text.isNullOrEmpty()) {
                    analyzeNewMessage(text)
                }
            }
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E))
    ) {
        // 메시지 리스트 (스크롤 가능) - 맨 아래 레이어
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 130.dp, start = 20.dp, end = 20.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            items(messageList, key = { it.id }) { item ->
                Column {
                    // 사용자 메시지
                    MessageBubble(message = item.message)
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // 분석 중 또는 결과
                    if (item.isAnalyzing) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = Color(0xFF2A2A2A),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(24.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(
                                        color = Color(0xFF4A9FF5),
                                        modifier = Modifier.size(24.dp),
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "메시지 분석 중...",
                                        color = Color.White,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    } else if (item.result != null) {
                        AnalysisResultCard(result = item.result)
                    }
                }
            }
        }
        
        // 상단 바 - 위 레이어
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter)
                .background(Color(0xFF1E1E1E)),
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
        
        // 상단 고정: 메시지 붙여넣기 버튼 (상단 바 바로 아래) - 위 레이어
        Button(
            onClick = { pasteFromClipboard() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 20.dp, top = 76.dp)
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
        
        // 하단 네비게이션 바 (홈 화면과 동일) - 위 레이어
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

/**
 * 사용자가 보낸 메시지를 표시하는 말풍선 (오른쪽 정렬)
 * 
 * @param message 표시할 메시지 내용
 */
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

/**
 * 분석 결과를 표시하는 카드 (왼쪽 정렬)
 * 
 * @param result 분석 결과 데이터
 */
@Composable
fun AnalysisResultCard(result: AnalysisResult) {
    // riskLevel에 따른 색상과 텍스트
    val (statusColor, statusText, statusIcon) = when (result.riskLevel) {
        RiskLevel.NORMAL -> Triple(
            Color(0xFF4A9FF5),  // 파란색 (안전)
            "정상",
            R.drawable.forward  // TODO: 체크마크 아이콘으로 변경
        )
        RiskLevel.WARN -> Triple(
            Color(0xFFFFA726),  // 주황색 (경고)
            "경고",
            R.drawable.forward  // TODO: 경고 아이콘으로 변경
        )
        RiskLevel.DANGER -> Triple(
            Color(0xFFFF6B6B),  // 빨간색 (위험)
            "위험",
            R.drawable.forward  // TODO: 위험 아이콘으로 변경
        )
    }
    
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
            // 아이콘과 결과 텍스트
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    painter = painterResource(id = statusIcon),
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = statusText,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = statusColor
                )
            }
            
            // 메인 메시지
            Text(
                text = when (result.riskLevel) {
                    RiskLevel.NORMAL -> "스미싱이 아닐 가능성이 ${result.confidence}"
                    RiskLevel.WARN -> "주의가 필요한 메시지입니다 (신뢰도: ${result.confidence})"
                    RiskLevel.DANGER -> "스미싱일 가능성이 ${result.confidence}"
                },
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = when (result.riskLevel) {
                    RiskLevel.NORMAL -> "이 문자는 스미싱이 아닙니다."
                    RiskLevel.WARN -> "링크 클릭 시 주의가 필요합니다."
                    RiskLevel.DANGER -> "이 문자는 스미싱입니다."
                },
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

/**
 * 분석 결과의 세부 정보 행 (라벨 + 값)
 * 
 * @param label 정보 라벨 (예: "발신자:", "내용:")
 * @param value 정보 값
 */
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

@Preview(showBackground = true, name = "분석 화면 (연속 분석)")
@Composable
fun AnalysisScreenPreview() {
    IACE2_FrontendTheme {
        AnalysisScreen(
            initialMessage = "[Web발신]\n(광고)[KB국민카드] 데이터 분석 부트캠프\n\n최대 170만원 지원금과 인턴십 기회까지, 데이터 분석 부트캠프",
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true, name = "메시지 말풍선")
@Composable
fun MessageBubblePreview() {
    IACE2_FrontendTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E1E1E))
                .padding(16.dp)
        ) {
            MessageBubble(message = "[Web발신]\n(광고)[KB국민카드] 데이터 분석 부트캠프")
        }
    }
}

@Preview(showBackground = true, name = "분석 결과 카드 - 정상 (NORMAL)")
@Composable
fun AnalysisResultCardNormalPreview() {
    IACE2_FrontendTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E1E1E))
                .padding(16.dp)
        ) {
            AnalysisResultCard(
                result = AnalysisResult(
                    riskLevel = RiskLevel.NORMAL,
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

@Preview(showBackground = true, name = "분석 결과 카드 - 경고 (WARN)")
@Composable
fun AnalysisResultCardWarnPreview() {
    IACE2_FrontendTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E1E1E))
                .padding(16.dp)
        ) {
            AnalysisResultCard(
                result = AnalysisResult(
                    riskLevel = RiskLevel.WARN,
                    isSmishing = false,
                    confidence = "보통",
                    sender = "번호 정보 확인 필요",
                    content = "일부 의심스러운 문구 포함",
                    links = "링크 주의 필요",
                    category = "주의가 필요한 메시지입니다. 링크 클릭에 유의하세요."
                )
            )
        }
    }
}

@Preview(showBackground = true, name = "분석 결과 카드 - 위험 (DANGER)")
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
                    riskLevel = RiskLevel.DANGER,
                    isSmishing = true,
                    confidence = "매우 높아요",
                    sender = "미확인 발신번호",
                    content = "개인정보 및 금융정보 요구 확인",
                    links = "의심스러운 도메인 감지",
                    category = "스미싱 위험 문자로 분류되었습니다. 절대 링크를 클릭하지 마세요!"
                )
            )
        }
    }
}

