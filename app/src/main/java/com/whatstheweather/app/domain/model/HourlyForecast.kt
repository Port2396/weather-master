package com.whatstheweather.app.domain.model

data class HourlyForecast(
    val time: String,
    val temperature: Double,
    val condition: WeatherCondition,
    val precipitationProbability: Int,
    val windSpeed: Double
)
