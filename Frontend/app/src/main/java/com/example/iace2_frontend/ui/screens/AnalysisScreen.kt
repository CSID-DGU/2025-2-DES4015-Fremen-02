package com.example.iace2_frontend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.iace2_frontend.R
import com.example.iace2_frontend.data.models.AnalysisResult
import com.example.iace2_frontend.ui.theme.IACE2_FrontendTheme
import kotlinx.coroutines.delay

/**
 * 메시지 분석 화면
 * 
 * 사용자가 붙여넣은 메시지를 분석하고 결과를 표시
 * 
 * @param message 분석할 메시지
 * @param onBackClick 뒤로가기 버튼 클릭 시 콜백
 */
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
                analysisResult?.let { result ->
                    AnalysisResultCard(result = result)
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
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
                    sender = "미확인 발신번호",
                    content = "개인정보 및 금융정보 요구 확인",
                    links = "의심스러운 도메인 감지",
                    category = "스미싱 위험 문자로 분류되었습니다."
                )
            )
        }
    }
}

