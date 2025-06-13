package com.dannav.mibancamit.utils

import android.util.Base64
import com.dannav.mibancamit.BuildConfig
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

object CryptoUtils {

    private const val AES_MODE = "AES/GCM/NoPadding"
    private const val IV_SIZE  = 12
    private const val TAG_SIZE = 128

    private val keyBytes: ByteArray by lazy {
        Base64.decode(BuildConfig.CRYPTO_KEY, Base64.DEFAULT)
    }

    private val secretKey: SecretKey by lazy {
        SecretKeySpec(keyBytes, "AES")
    }

    data class Enc(val iv: String, val cipherText: String)

    /** Devuelve IV y texto cifrado, ambos Base64 */
    fun encrypt(plain: String): Enc {
        val iv = ByteArray(IV_SIZE).also { SecureRandom().nextBytes(it) }

        val cipher = Cipher.getInstance(AES_MODE).apply {
            init(Cipher.ENCRYPT_MODE, secretKey, GCMParameterSpec(TAG_SIZE, iv))
        }

        val cipherBytes = cipher.doFinal(plain.toByteArray(Charsets.UTF_8))
        return Enc(
            iv          = Base64.encodeToString(iv, Base64.NO_WRAP),
            cipherText  = Base64.encodeToString(cipherBytes, Base64.NO_WRAP)
        )
    }

    fun decrypt(enc: Enc): String {
        val iv  = Base64.decode(enc.iv,          Base64.DEFAULT)
        val ct  = Base64.decode(enc.cipherText,  Base64.DEFAULT)

        val cipher = Cipher.getInstance(AES_MODE).apply {
            init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(TAG_SIZE, iv))
        }
        val bytes = cipher.doFinal(ct)
        return bytes.toString(Charsets.UTF_8)
    }
}
