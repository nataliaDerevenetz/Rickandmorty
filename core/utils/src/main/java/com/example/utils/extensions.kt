package com.example.utils

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

fun Long.timestampToString(): String {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val instant = Instant.ofEpochSecond(this)
    val date = instant.atZone(ZoneId.systemDefault()).toLocalDate()
    return date.format(formatter)
}

fun String.toTimestamp(pattern: String): Long {
    val formatter = DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH)
    val date = LocalDate.parse(this, formatter)
    val dateTime = date.atStartOfDay(ZoneOffset.UTC)
    return dateTime.toEpochSecond()
}

