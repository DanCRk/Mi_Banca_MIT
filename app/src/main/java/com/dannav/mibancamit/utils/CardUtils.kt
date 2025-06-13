package com.dannav.mibancamit.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

object CardUtils {

    fun detectCardType(number: String): String {
        val clean = number.filter { it.isDigit() }

        return when {
            clean.startsWith("4") -> "Visa"
            clean.matches(Regex("^5[1-5].*")) -> "MasterCard"
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



    object CardVisualTransformation : VisualTransformation {
        override fun filter(text: AnnotatedString): TransformedText {
            val trimmed = text.text.filter { it.isDigit() }.take(16)
            val formatted = trimmed.chunked(4).joinToString(" ")

            val offsetMap = object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    var extra = (0 until offset).count { (it + 1) % 4 == 0 && it < trimmed.length - 1 }
                    return offset + extra
                }

                override fun transformedToOriginal(offset: Int): Int {
                    return offset - (0 until offset).count { (it + 1) % 5 == 0 }
                }
            }

            return TransformedText(AnnotatedString(formatted), offsetMap)
        }
    }

    object ExpiryVisualTransformation : VisualTransformation {
        override fun filter(text: AnnotatedString): TransformedText {
            val raw = text.text.filter { it.isDigit() }.take(4)

            val formatted = when {
                raw.length >= 3 -> raw.substring(0, 2) + "/" + raw.substring(2)
                else            -> raw
            }

            val offsetMap = object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    return when {
                        offset <= 2 -> offset
                        offset <= 4 -> offset + 1
                        else        -> 5
                    }
                }

                override fun transformedToOriginal(offset: Int): Int {
                    return when {
                        offset <= 2 -> offset
                        offset <= 5 -> offset - 1
                        else        -> 4
                    }
                }
            }

            return TransformedText(AnnotatedString(formatted), offsetMap)
        }
    }



}