package com.whatstheweather.app.presentation.util

import com.whatstheweather.app.domain.model.TimeFormat

/**
 * Formats a "HH:mm" time string (24-hour from the Open-Meteo API) according
 * to the user's preferred 12h / 24h format.
 *
 * Input examples: "06:30", "18:45"
 * Output examples: 24h → "06:30" / "18:45"
 *                  12h → "6:30 AM" / "6:45 PM"
 */
fun formatTime(time: String, format: TimeFormat): String {
    if (time.isBlank()) return time
    val parts = time.split(":")
    if (parts.size < 2) return time

    val hour = parts[0].toIntOrNull() ?: return time
    val minute = parts[1].padStart(2, '0')

    return when (format) {
        TimeFormat.HOUR_24 -> "${parts[0].padStart(2, '0')}:$minute"
        TimeFormat.HOUR_12 -> {
            val suffix = if (hour < 12) "AM" else "PM"
            val displayHour = when {
                hour == 0 -> 12
                hour > 12 -> hour - 12
                else -> hour
            }
            "$displayHour:$minute $suffix"
        }
    }
}
