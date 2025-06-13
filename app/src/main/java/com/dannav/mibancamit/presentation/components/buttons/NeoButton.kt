package com.dannav.mibancamit.presentation.components.buttons

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dannav.mibancamit.ui.theme.BackgroundButtonDarkShadow
import com.dannav.mibancamit.ui.theme.BackgroundButtonLightShadow
import com.dannav.mibancamit.ui.theme.BackgroundColor
import com.dannav.mibancamit.ui.theme.ColorText
import com.gandiva.neumorphic.LightSource
import com.gandiva.neumorphic.neu
import com.gandiva.neumorphic.shape.Flat
import com.gandiva.neumorphic.shape.Pressed
import com.gandiva.neumorphic.shape.RoundedCorner

@Composable
fun NeomorphismButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String,
    textStyle: TextStyle = TextStyle(),
    fontSize: Int  = 16,
    onClick: () -> Unit,

) {


    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Button(
        enabled = enabled,
        modifier = modifier
            .neu(
                lightShadowColor = BackgroundButtonLightShadow,
                darkShadowColor = BackgroundButtonDarkShadow,
                shadowElevation = 6.dp,
                lightSource = LightSource.LEFT_TOP,
                shape = if (isPressed || !enabled) Pressed(RoundedCorner(25.dp)) else Flat(
                    RoundedCorner(25.dp)
                ),
            ),
        onClick = { onClick() },
        interactionSource = interactionSource,
        colors = ButtonDefaults.buttonColors(
            containerColor = BackgroundColor,
            contentColor = Color.Transparent,
            disabledContainerColor = BackgroundColor,
            disabledContentColor = BackgroundColor
        ),

        ) {
        Text(text = text, color = ColorText, fontSize = fontSize.sp, style = textStyle)
    }
}