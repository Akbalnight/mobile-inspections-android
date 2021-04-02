package ru.madbrains.data.extensions

import java.text.SimpleDateFormat
import java.util.*

fun Date.toDDMMYYYY(): String {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return dateFormat.format(this)
}
fun Date.toYYYYMMDD(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return dateFormat.format(this)
}
fun Date.toHHmmYYYYMMDD(): String {
    val dateFormat = SimpleDateFormat("HH:mm  dd.MM.yyyy", Locale.getDefault())
    return dateFormat.format(this)
}

fun Date.toyyyyMMddTHHmmssXXX(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())
    return dateFormat.format(this)
}

fun Date.toHHmm(): String {
    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return dateFormat.format(this)
}

fun Date.toddMMyyyyHHmm(): String {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    return dateFormat.format(this)
}