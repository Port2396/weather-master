package com.whatstheweather.app.data.api.openmeteo

import retrofit2.http.GET
import retrofit2.http.Query

interface OpenMeteoService {

    @GET("forecast")
    suspend fun getForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = CURRENT_VARS,
        @Query("hourly") hourly: String = HOURLY_VARS,
        @Query("daily") daily: String = DAILY_VARS,
        @Query("forecast_days") forecastDays: Int = 10,
        @Query("timezone") timezone: String = "auto"
    ): OpenMeteoForecastResponse

    companion object {
        const val BASE_URL = "https://api.open-meteo.com/v1/"

        private const val CURRENT_VARS =
            "temperature_2m,relative_humidity_2m,apparent_temperature," +
            "is_day,precipitation,rain,weather_code," +
            "wind_speed_10m,wind_direction_10m"

        private const val HOURLY_VARS =
            "temperature_2m,precipitation_probability,weather_code,wind_speed_10m"

        private const val DAILY_VARS =
            "weather_code,temperature_2m_max,temperature_2m_min," +
            "sunrise,sunset,uv_index_max,precipitation_probability_max"
    }
}

interface OpenMeteoAirQualityService {

    @GET("air-quality")
    suspend fun getAirQuality(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "european_aqi,us_aqi,pm2_5,pm10",
        @Query("timezone") timezone: String = "auto"
    ): OpenMeteoAirQualityResponse

    companion object {
        const val BASE_URL = "https://air-quality-api.open-meteo.com/v1/"
    }
}

interface OpenMeteoGeocodingService {

    @GET("search")
    suspend fun searchCities(
        @Query("name") name: String,
        @Query("count") count: Int = 10,
        @Query("language") language: String = "en",
        @Query("format") format: String = "json"
    ): GeocodingResponse

    companion object {
        const val BASE_URL = "https://geocoding-api.open-meteo.com/v1/"
    }
}
