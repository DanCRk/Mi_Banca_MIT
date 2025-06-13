package com.dannav.mibancamit.data.model

import androidx.compose.ui.graphics.Brush
data class Card(
    val cardId: String = "",
    val cardType: String,
    val cardNumber: String,
    val cardName: String,
    val balance: Double,
    val color: Brush
)
