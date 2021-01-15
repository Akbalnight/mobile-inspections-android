package ru.madbrains.data.extensions

import android.util.Base64
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.*

fun String.toBase64HashWith256(): String {
    val digest = MessageDigest.getInstance("SHA-256")
    val byteOfTextToHash = this.toByteArray()
    val hashedByteArray = digest.digest(byteOfTextToHash)
    val hashedString =
        String.format("%0" + (hashedByteArray.size * 2) + "X", BigInteger(1, hashedByteArray))
    return hashedString.toLowerCase(Locale.getDefault()).toBase64Hash()
}

fun String.toBase64Hash(): String {
    val encodedBytes = Base64.encode(this.toByteArray(), Base64.NO_WRAP)
    return String(encodedBytes, StandardCharsets.UTF_8)
}