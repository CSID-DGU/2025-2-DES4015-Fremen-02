package com.example.iace2_frontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.iace2_frontend.ui.theme.IACE2_FrontendTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IACE2_FrontendTheme {
                MyApp()
            }
        }
    }
}

@Composable
fun MyApp() {
    var text by remember { mutableStateOf("Hello Android!") }

    // 화면 중앙에 텍스트와 버튼 배치
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = text)
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = { text = "버튼 눌렀다!" }) {
            Text("눌러봐")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyAppPreview() {
    IACE2_FrontendTheme {
        MyApp()
    }
}
