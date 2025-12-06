package com.example.appquanlycv.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.appquanlycv.R

@Composable
fun AuthScreen(
    isLoading: Boolean,
    errorMessage: String?,
    onGoogleLogin: () -> Unit,
    onFacebookLogin: () -> Unit
) {
    val gradient = Brush.verticalGradient(
        listOf(
            Color(0xFFFFF0F5),
            Color(0xFFFDE7F3),
            Color(0xFFF5F9FF)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logoapp),
                    contentDescription = null,
                    modifier = Modifier.height(120.dp)
                )
                Text(
                    text = stringResource(id = R.string.auth_title),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF6C4A93),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(id = R.string.auth_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF6C4A93),
                    textAlign = TextAlign.Center
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFB00020),
                        textAlign = TextAlign.Center
                    )
                }

                if (isLoading) {
                    CircularProgressIndicator(color = Color(0xFF6C4A93))
                    Text(text = stringResource(id = R.string.auth_loading), color = Color(0xFF6C4A93))
                } else {
                    Button(
                        onClick = onGoogleLogin,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDB4437)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = stringResource(id = R.string.auth_google), color = Color.White)
                    }

                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
