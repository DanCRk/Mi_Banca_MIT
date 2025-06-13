package com.dannav.mibancamit.data.model

data class Transaction(
    val fromUid: String = "",
    val toUid: String = "",
    val fromCardId: String = "",
    val toCardId: String = "",
    val fromCardName: String = "",  // Nombre de la tarjeta desde la que se realizó el pago
    val amount: Double = 0.0,
    val timestamp: Long = 0L,
    val type: String = ""           // "Depósito" o "Pago"
)
