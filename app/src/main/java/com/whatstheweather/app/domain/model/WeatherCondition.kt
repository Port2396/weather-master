package com.whatstheweather.app.domain.model

enum class WeatherCondition {
    CLEAR_SKY, PARTLY_CLOUDY, OVERCAST,
    FOG, DRIZZLE, RAIN, SNOW,
    RAIN_SHOWERS, SNOW_SHOWERS,
    THUNDERSTORM, THUNDERSTORM_WITH_HAIL,
    UNKNOWN
}

fun Int.toWeatherCondition(): WeatherCondition = when (this) {
    0 -> WeatherCondition.CLEAR_SKY
    1, 2 -> WeatherCondition.PARTLY_CLOUDY
    3 -> WeatherCondition.OVERCAST
    45, 48 -> WeatherCondition.FOG
    51, 53, 55 -> WeatherCondition.DRIZZLE
    61, 63, 65 -> WeatherCondition.RAIN
    71, 73, 75, 77 -> WeatherCondition.SNOW
    80, 81, 82 -> WeatherCondition.RAIN_SHOWERS
    85, 86 -> WeatherCondition.SNOW_SHOWERS
    95 -> WeatherCondition.THUNDERSTORM
    96, 99 -> WeatherCondition.THUNDERSTORM_WITH_HAIL
    else -> WeatherCondition.UNKNOWN
}

fun WeatherCondition.toDisplayName(): String = when (this) {
    WeatherCondition.CLEAR_SKY -> "Clear Sky"
    WeatherCondition.PARTLY_CLOUDY -> "Partly Cloudy"
    WeatherCondition.OVERCAST -> "Overcast"
    WeatherCondition.FOG -> "Foggy"
    WeatherCondition.DRIZZLE -> "Drizzle"
    WeatherCondition.RAIN -> "Rain"
    WeatherCondition.SNOW -> "Snow"
    WeatherCondition.RAIN_SHOWERS -> "Rain Showers"
    WeatherCondition.SNOW_SHOWERS -> "Snow Showers"
    WeatherCondition.THUNDERSTORM -> "Thunderstorm"
    WeatherCondition.THUNDERSTORM_WITH_HAIL -> "Thunderstorm with Hail"
    WeatherCondition.UNKNOWN -> "Unknown"
}
