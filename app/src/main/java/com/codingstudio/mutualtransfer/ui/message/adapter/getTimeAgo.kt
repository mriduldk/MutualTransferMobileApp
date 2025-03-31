package com.codingstudio.mutualtransfer.ui.message.adapter

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.OffsetDateTime
import java.time.Duration
import java.time.ZoneId
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@RequiresApi(Build.VERSION_CODES.O)
fun getTimeAgo(timestamp: String): String {
    val time = OffsetDateTime.parse(timestamp)
    val now = OffsetDateTime.now(ZoneId.of("UTC"))

    val duration = Duration.between(time, now)

    return when {
        duration.toMinutes() < 1 -> "Just now"
        duration.toMinutes() < 60 -> "${duration.toMinutes()} minutes ago"
        duration.toHours() < 24 -> "${duration.toHours()} hours ago"
        duration.toDays() < 7 -> "${duration.toDays()} days ago"
        duration.toDays() < 30 -> "${duration.toDays() / 7} weeks ago"
        duration.toDays() < 365 -> "${duration.toDays() / 30} months ago"
        else -> "${duration.toDays() / 365} years ago"
    }
}

fun getTimeAgo2(timestamp: String): String {
    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
    format.timeZone = TimeZone.getTimeZone("UTC") // Parse the time in UTC

    val time: Date = format.parse(timestamp) ?: return "Unknown time"
    val now = Date()

    val diff = now.time - time.time

    return when {
        diff < TimeUnit.MINUTES.toMillis(1) -> "Just now"
        diff < TimeUnit.HOURS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toMinutes(diff)} minutes ago"
        diff < TimeUnit.DAYS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toHours(diff)} hours ago"
        diff < TimeUnit.DAYS.toMillis(7) -> "${TimeUnit.MILLISECONDS.toDays(diff)} days ago"
        diff < TimeUnit.DAYS.toMillis(30) -> "${TimeUnit.MILLISECONDS.toDays(diff) / 7} weeks ago"
        diff < TimeUnit.DAYS.toMillis(365) -> "${TimeUnit.MILLISECONDS.toDays(diff) / 30} months ago"
        else -> "${TimeUnit.MILLISECONDS.toDays(diff) / 365} years ago"
    }
}