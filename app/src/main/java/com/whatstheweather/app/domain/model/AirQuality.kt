package com.whatstheweather.app.domain.model

data class AirQuality(
    val europeanAqi: Int,
    val usAqi: Int,
    val pm25: Double,
    val pm10: Double
)

fun AirQuality.getCategory(): String = when (europeanAqi) {
    in 0..20 -> "Good"
    in 21..40 -> "Fair"
    in 41..60 -> "Moderate"
    in 61..80 -> "Poor"
    in 81..100 -> "Very Poor"
    else -> "Extremely Poor"
}
