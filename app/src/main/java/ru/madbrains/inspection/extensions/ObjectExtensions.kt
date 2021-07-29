package ru.madbrains.inspection.extensions

import org.threeten.bp.DateTimeUtils
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import java.util.*

fun LocalDate.toDate(): Date {
    val instant = this.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()
    return DateTimeUtils.toDate(instant)
}

fun Date.toLocalDate(): LocalDate {
    return Instant.ofEpochMilli(time).atZone(ZoneId.systemDefault()).toLocalDate()
}