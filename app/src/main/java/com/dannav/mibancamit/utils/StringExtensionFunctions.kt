package com.dannav.mibancamit.utils

import java.util.regex.Pattern

/** e-mail RFC 5322 (versión corta) */
fun String.isValidEmail(): Boolean =
    Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$").matches(this)

/** username ≥ 6 caracteres, únicamente letras o números */
fun String.isValidUsername(): Boolean =
    length >= 6 && all { it.isLetterOrDigit() }

/** contraseña ≥ 6 caracteres, únicamente letras o números */
fun String.isValidPassword(): Boolean =
    length >= 6 && all { it.isLetterOrDigit() }
