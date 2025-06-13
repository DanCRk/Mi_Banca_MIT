package com.dannav.mibancamit.utils

object CardUtils {

    fun detectCardType(number: String): String {
        val clean = number.filter { it.isDigit() }

        return when {
            clean.startsWith("4")                   -> "Visa"
            clean.matches(Regex("^5[1-5].*"))       -> "MasterCard"
            else -> "Desconocida"
        }
    }

    fun nameOk(name: String) =
        name.trim().length >= 6

    fun numberOk(number: String): Boolean {
        val digits = number.filter(Char::isDigit)
        return digits.length in 13..19
    }

    fun expiryOk(exp: String): Boolean =
        Regex("""^(0[1-9]|1[0-2])/(\d{2})$""").matches(exp)
}