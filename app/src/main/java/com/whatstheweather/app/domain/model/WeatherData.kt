package com.whatstheweather.app.domain.model

data class WeatherData(
    val city: City,
    val currentTemp: Double,
    val feelsLike: Double,
    val tempMax: Double,
    val tempMin: Double,
    val condition: WeatherCondition,
    val humidity: Int,
    val windSpeed: Double,
    val windDirection: Int,
    val precipitation: Double,
    val uvIndex: Double,
    val sunrise: String,
    val sunset: String,
    val isDay: Boolean,
    val hourlyForecast: List<HourlyForecast>,
    val dailyForecast: List<DailyForecast>,
    val airQuality: AirQuality?,
    val lastUpdated: Long = System.currentTimeMillis()
)
