package com.dannav.mibancamit.utils

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object ColorUtils {
    fun getCardColor(cardType: String): Brush =
        when (cardType.lowercase()) {
            "visa"     -> Brush.horizontalGradient(listOf(Color(0xFF1A1F71), Color(0xFF00A0E9)))
            "mastercard" -> Brush.linearGradient(listOf(Color.Red, Color(0xFFFF9500)))
            "amex"     -> Brush.linearGradient(listOf(Color(0xFF008080), Color.LightGray))
            else       -> Brush.verticalGradient(listOf(Color.Gray, Color.DarkGray))
        }

}