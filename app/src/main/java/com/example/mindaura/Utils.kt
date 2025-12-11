package com.example.mindaura

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

/**
 * Converts a timestamp in milliseconds to a date string in the format `yyyy-MM-dd`.
 *
 * @receiver Timestamp in milliseconds.
 * @return Formatted date string.
 */
@RequiresApi(Build.VERSION_CODES.O)
fun Long.toDateString(): String {
    val instant = Instant.ofEpochMilli(this)
    val date = instant.atZone(ZoneId.systemDefault()).toLocalDate()
    return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
}

/**
 * Converts a timestamp in milliseconds to a full date string
 * including day of the week, month, day, and year.
 * Example: "Wednesday, December 11, 2025".
 *
 * @receiver Timestamp in milliseconds.
 * @return Formatted full date string.
 */
@RequiresApi(Build.VERSION_CODES.O)
fun Long.toFullDateString(): String {
    val instant = Instant.ofEpochMilli(this)
    val date = instant.atZone(ZoneId.systemDefault()).toLocalDate()
    val formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.getDefault())
    return date.format(formatter)
}

/**
 * Converts a [LocalDate] to the timestamp in milliseconds representing the start of that day.
 *
 * @receiver LocalDate to convert.
 * @return Milliseconds at the start of the day.
 */
fun LocalDate.toDayMillis(): Long {
    return this.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
}


