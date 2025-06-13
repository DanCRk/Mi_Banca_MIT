package com.dannav.mibancamit.presentation.loading

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.dannav.mibancamit.ui.theme.BackgroundButtonLightShadow

@Composable
fun FullScreenProgressBar(text: String) {
    val backgroundColor = BackgroundButtonLightShadow
    val textColor = Color.White
    val fontSize = 20.sp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor.copy(alpha = 0.6f))
            .clickable(enabled = true) {}
            .zIndex(20f)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = text,
                color = textColor,
                fontSize = fontSize
            )

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = Color.White)
        }
    }
}
