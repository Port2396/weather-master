package com.whatstheweather.app.presentation.util

import com.whatstheweather.app.domain.model.TemperatureUnit
import com.whatstheweather.app.domain.model.WindSpeedUnit
import kotlin.math.roundToInt

/**
 * Pure display-layer formatters. The domain layer always stores temperature
 * in Celsius and wind speed in km/h — these helpers convert at render time
 * based on user preference.
 */

fun formatTemperature(
    celsius: Double,
    unit: TemperatureUnit,
    withSymbol: Boolean = false
): String {
    val value = when (unit) {
        TemperatureUnit.CELSIUS -> celsius
        TemperatureUnit.FAHRENHEIT -> celsius * 9.0 / 5.0 + 32.0
    }
    val suffix = if (withSymbol) {
        when (unit) {
            TemperatureUnit.CELSIUS -> "°C"
            TemperatureUnit.FAHRENHEIT -> "°F"
        }
    } else "°"
    return "${value.roundToInt()}$suffix"
}

fun formatWindSpeed(kmh: Double, unit: WindSpeedUnit): String {
    return when (unit) {
        WindSpeedUnit.KMH -> "${kmh.roundToInt()} km/h"
        WindSpeedUnit.MPH -> "${(kmh * 0.621371).roundToInt()} mph"
        WindSpeedUnit.MS -> "${(kmh / 3.6).roundToInt()} m/s"
    }
}
