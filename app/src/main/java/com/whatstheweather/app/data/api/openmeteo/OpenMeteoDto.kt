package com.whatstheweather.app.data.api.openmeteo

import com.google.gson.annotations.SerializedName

// ─── Forecast Response ────────────────────────────────────────────────────────

data class OpenMeteoForecastResponse(
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val current: CurrentWeatherDto,
    val hourly: HourlyWeatherDto,
    val daily: DailyWeatherDto
)

data class CurrentWeatherDto(
    val time: String,
    @SerializedName("temperature_2m") val temperature: Double,
    @SerializedName("relative_humidity_2m") val humidity: Int,
    @SerializedName("apparent_temperature") val apparentTemperature: Double,
    @SerializedName("is_day") val isDay: Int,
    val precipitation: Double,
    val rain: Double,
    @SerializedName("weather_code") val weatherCode: Int,
    @SerializedName("wind_speed_10m") val windSpeed: Double,
    @SerializedName("wind_direction_10m") val windDirection: Int
)

data class HourlyWeatherDto(
    val time: List<String>,
    @SerializedName("temperature_2m") val temperature: List<Double>,
    @SerializedName("precipitation_probability") val precipitationProbability: List<Int>,
    @SerializedName("weather_code") val weatherCode: List<Int>,
    @SerializedName("wind_speed_10m") val windSpeed: List<Double>
)

data class DailyWeatherDto(
    val time: List<String>,
    @SerializedName("weather_code") val weatherCode: List<Int>,
    @SerializedName("temperature_2m_max") val temperatureMax: List<Double>,
    @SerializedName("temperature_2m_min") val temperatureMin: List<Double>,
    val sunrise: List<String>,
    val sunset: List<String>,
    @SerializedName("uv_index_max") val uvIndexMax: List<Double>,
    @SerializedName("precipitation_probability_max") val precipitationProbabilityMax: List<Int>
)

// ─── Air Quality Response ─────────────────────────────────────────────────────

data class OpenMeteoAirQualityResponse(
    val latitude: Double,
    val longitude: Double,
    val current: AirQualityCurrentDto
)

data class AirQualityCurrentDto(
    @SerializedName("european_aqi") val europeanAqi: Int,
    @SerializedName("us_aqi") val usAqi: Int,
    @SerializedName("pm2_5") val pm25: Double,
    val pm10: Double
)

// ─── Geocoding Response ───────────────────────────────────────────────────────

data class GeocodingResponse(
    val results: List<GeocodingResultDto>?
)

data class GeocodingResultDto(
    val id: Long,
    val name: String,
    val country: String,
    @SerializedName("admin1") val admin1: String?,
    val latitude: Double,
    val longitude: Double
)
