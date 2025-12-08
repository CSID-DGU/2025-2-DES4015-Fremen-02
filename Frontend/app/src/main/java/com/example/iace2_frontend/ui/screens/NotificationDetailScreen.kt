package com.example.iace2_frontend.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.iace2_frontend.R
import com.example.iace2_frontend.data.models.AnalysisResult
import com.example.iace2_frontend.data.models.RiskLevel

/**
 * 알림 상세 화면
 * 
 * 푸시 알림 클릭 시 표시되는 화면으로, 이미 분석된 결과를 보여줍니다.
 * 서버에 재요청하지 않고 전달받은 데이터만 표시합니다.
 * 
 * @param analysisResult 분석 결과
 * @param originalMessage 원본 메시지
 * @param senderNumber 발신자 번호
 * @param onBackClick 뒤로가기 버튼 클릭 시 콜백
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationDetailScreen(
    analysisResult: AnalysisResult,
    originalMessage: String,
    senderNumber: String,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("문자 분석 결과") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 위험 레벨 표시
            RiskLevelCard(analysisResult.riskLevel)
            
            // 발신자 정보
            InfoCard(
                title = "발신자",
                content = senderNumber
            )
            
            // 원본 메시지
            InfoCard(
                title = "원본 메시지",
                content = originalMessage
            )

            // 판단 근거 & 후속 조치 텍스트
            val (reasonText, actionText) = when (analysisResult.riskLevel) {
                RiskLevel.DANGER -> Pair(
                    "메시지에 도박·사기성 키워드, 과도한 혜택(예: 100% 보장), " +
                            "의심스러운 링크 주소 등이 함께 포함되어 있어 관련 법령과 금융 보안 지침상 " +
                            "스미싱 문자일 가능성이 매우 높은 패턴으로 판단됩니다.",
                    "문자에 포함된 링크를 절대 누르지 말고, 계좌번호·비밀번호·인증번호 등 어떤 정보도 입력하지 마세요. " +
                            "이미 클릭했다면 금융기관 공식 앱이나 홈페이지를 통해 즉시 비밀번호를 변경하고, " +
                            "해당 기관 고객센터나 금융감독원(국번 없이 1332), 한국인터넷진흥원(118) 등 공식 채널로 " +
                            "사실 여부를 문의하는 것을 권장드립니다."
                )

                RiskLevel.WARN -> Pair(
                    "홍보·안내 문자로도 보이지만, 계좌 확인을 요청하거나 링크 접속을 유도하는 등 일부 내용이 " +
                            "의심스러운 표현을 포함하고 있어 주의가 필요한 메시지로 판단됩니다.",
                    "발신자가 신뢰할 만한 번호인지 다시 한 번 확인하시고, 문자에 포함된 링크보다는 " +
                            "직접 해당 기관의 공식 앱 또는 홈페이지에 접속해 내용을 확인하는 것이 안전합니다. " +
                            "조금이라도 이상하다고 느껴지면 문자에 답장하거나 링크를 누르지 말고, " +
                            "공식 고객센터로 직접 문의해 주세요."
                )

                RiskLevel.NORMAL -> Pair(
                    "개인정보나 금융정보 입력을 요구하는 문장, 계좌 확인·송금 요청, " +
                            "의심스러운 링크 등 스미싱에서 자주 보이는 요소가 확인되지 않았으며, " +
                            "일반적인 안내·홍보 목적의 내용으로 판단되었습니다.",
                    "이 문자는 스미싱 가능성이 낮지만, 금융 거래와 관련된 중요한 내용은 " +
                            "항상 공식 앱이나 홈페이지, 고객센터를 통해 한 번 더 확인하는 습관을 가지면 " +
                            "더 안전하게 이용하실 수 있습니다."
                )
            }

            // 판단 근거 카드
            AnalysisResultCard(
                title = "판단 근거",
                content = buildString {
                    append(reasonText)
                    analysisResult.reason?.let {
                        append("\n\n· AI 분석 참고: ")
                        append(it)
                    }
                }
            )

            // 후속 조치 카드
            AnalysisResultCard(
                title = "후속 조치",
                content = actionText
            )

            // 위험 점수
            if (analysisResult.riskScore != null) {
                InfoCard(
                    title = "위험 점수",
                    content = "${analysisResult.riskScore}점 (${analysisResult.confidence ?: "알 수 없음"})"
                )
            }

            // 카테고리
            if (analysisResult.category != null) {
                InfoCard(
                    title = "카테고리",
                    content = getCategoryKorean(analysisResult.category!!)
                )
            }

        }
    }
}

@Composable
private fun RiskLevelCard(riskLevel: RiskLevel) {
    val cardData = when (riskLevel) {
        RiskLevel.DANGER -> CardData(
            backgroundColor = Color(0xFFFF6B6B),
            textColor = Color.White,
            icon = R.drawable.danger,
            title = "⚠️ 위험"
        )
        RiskLevel.WARN -> CardData(
            backgroundColor = Color(0xFFFFA726),
            textColor = Color.White,
            icon = R.drawable.warn,
            title = "⚠️ 경고"
        )
        RiskLevel.NORMAL -> CardData(
            backgroundColor = Color(0xFF4A9FF5),
            textColor = Color.White,
            icon = R.drawable.normal,
            title = "✓ 정상"
        )
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardData.backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = cardData.icon),
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
            Text(
                text = cardData.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = cardData.textColor
            )
        }
    }
}

private data class CardData(
    val backgroundColor: Color,
    val textColor: Color,
    val icon: Int,
    val title: String
)

@Composable
private fun InfoCard(title: String, content: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF999999)
            )
            Text(
                text = content,
                fontSize = 16.sp,
                color = Color.White,
                lineHeight = 24.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * 카테고리를 한국어로 변환
 */
private fun getCategoryKorean(category: String): String {
    return when (category.uppercase()) {
        "GAMBLING" -> "도박"
        "IMPERSONATION" -> "사칭"
        "NORMAL" -> "정상"
        "SMISHING" -> "스미싱"
        else -> category // 알 수 없는 카테고리는 그대로 표시
    }
}

@Composable
private fun AnalysisResultCard(title: String, content: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF999999)
            )
            // 전체 텍스트 표시 (잘림 방지)
            Text(
                text = content,
                fontSize = 16.sp,
                color = Color.White,
                lineHeight = 24.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

