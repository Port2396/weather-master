package com.whatstheweather.app.domain.model

enum class TemperatureUnit { CELSIUS, FAHRENHEIT }
enum class WindSpeedUnit { KMH, MPH, MS }
enum class TimeFormat { HOUR_12, HOUR_24 }
enum class AppTheme { SYSTEM, LIGHT, DARK }
enum class WeatherApiProvider { OPEN_METEO, OPEN_WEATHER_MAP, WEATHER_API, TOMORROW_IO }

data class AppSettings(
    val temperatureUnit: TemperatureUnit = TemperatureUnit.CELSIUS,
    val windSpeedUnit: WindSpeedUnit = WindSpeedUnit.KMH,
    val timeFormat: TimeFormat = TimeFormat.HOUR_12,
    val theme: AppTheme = AppTheme.SYSTEM,
    val apiProvider: WeatherApiProvider = WeatherApiProvider.OPEN_METEO,
    val openWeatherMapApiKey: String = "",
    val weatherApiKey: String = "",
    val tomorrowApiKey: String = "",
    val cacheMinutes: Int = 30,
    val notificationsEnabled: Boolean = false
)
