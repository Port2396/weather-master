package com.whatstheweather.app.domain.model

data class DailyForecast(
    val date: String,
    val dayName: String,
    val tempMax: Double,
    val tempMin: Double,
    val condition: WeatherCondition,
    val precipitationProbability: Int,
    val uvIndexMax: Double,
    val sunrise: String,
    val sunset: String
)
