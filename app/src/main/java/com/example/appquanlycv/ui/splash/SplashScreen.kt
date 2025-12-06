package com.example.appquanlycv.ui.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.appquanlycv.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinish: () -> Unit) {
    var visible by remember { mutableStateOf(false) }

    // Chạy hiệu ứng & chuyển màn
    LaunchedEffect(Unit) {
        visible = true
        delay(1500)
        visible = false
        delay(300)
        onFinish()
    }

    val gradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFFEAF4),
            Color(0xFFFDE7F3),
            Color(0xFFF5F9FF)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(800)),
            exit = fadeOut(tween(300))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logoapp),
                    contentDescription = "Logo",
                    modifier = Modifier.size(120.dp)
                )

                Text(
                    text = "RemindMe",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6C4A93)
                    )
                )

                Text(
                    text = "Trợ lý nhắc việc dễ thương",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF8B5FBF))
                )
            }
        }
    }
}
