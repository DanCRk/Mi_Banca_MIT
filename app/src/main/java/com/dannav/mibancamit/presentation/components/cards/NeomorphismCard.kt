package com.dannav.mibancamit.presentation.components.cards

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dannav.mibancamit.ui.theme.BackgroundButtonDarkShadow
import com.dannav.mibancamit.ui.theme.BackgroundButtonLightShadow
import com.dannav.mibancamit.ui.theme.BackgroundColor
import com.gandiva.neumorphic.LightSource
import com.gandiva.neumorphic.neu
import com.gandiva.neumorphic.shape.Flat
import com.gandiva.neumorphic.shape.RoundedCorner

@Composable
fun NeomorphismCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Card(
        colors = CardColors(
            contentColor = BackgroundColor,
            containerColor = BackgroundColor,
            disabledContainerColor = BackgroundColor,
            disabledContentColor = BackgroundColor
        ),
        modifier = modifier
            .neu(
                lightShadowColor = BackgroundButtonLightShadow,
                darkShadowColor = BackgroundButtonDarkShadow,
                shadowElevation = 6.dp,
                lightSource = LightSource.LEFT_TOP,
                shape = Flat(RoundedCorner(24.dp)),
            ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        ),
        shape = RoundedCornerShape(24.dp),
    ) {
        content()
    }
}