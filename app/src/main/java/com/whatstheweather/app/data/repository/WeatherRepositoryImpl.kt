package com.whatstheweather.app.data.repository

import com.whatstheweather.app.data.api.openmeteo.*
import com.whatstheweather.app.domain.model.*
import com.whatstheweather.app.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val forecastService: OpenMeteoService,
    private val airQualityService: OpenMeteoAirQualityService
) : WeatherRepository {

    override fun getWeather(city: City, forceRefresh: Boolean): Flow<Result<WeatherData>> = flow {
        runCatching {
            val forecast = forecastService.getForecast(city.latitude, city.longitude)
            val airQuality = runCatching {
                airQualityService.getAirQuality(city.latitude, city.longitude)
            }.getOrNull()

            forecast.toDomain(city, airQuality)
        }.also { emit(it) }
    }

    private fun OpenMeteoForecastResponse.toDomain(city: City, aqResponse: OpenMeteoAirQualityResponse?): WeatherData {
        val current = this.current
        val todayDaily = this.daily

        // Limit to 24 hourly entries
        val nowHour = LocalDateTime.now().hour
        val hourlyList = (0 until minOf(24, hourly.time.size)).map { i ->
            HourlyForecast(
                time = hourly.time[i].substringAfterLast("T"),
                temperature = hourly.temperature[i],
                condition = hourly.weatherCode[i].toWeatherCondition(),
                precipitationProbability = hourly.precipitationProbability.getOrElse(i) { 0 },
                windSpeed = hourly.windSpeed.getOrElse(i) { 0.0 }
            )
        }

        val dailyList = (0 until daily.time.size).map { i ->
            val date = LocalDate.parse(daily.time[i])
            DailyForecast(
                date = daily.time[i],
                dayName = if (i == 0) "Today" else date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                tempMax = daily.temperatureMax[i],
                tempMin = daily.temperatureMin[i],
                condition = daily.weatherCode[i].toWeatherCondition(),
                precipitationProbability = daily.precipitationProbabilityMax.getOrElse(i) { 0 },
                uvIndexMax = daily.uvIndexMax.getOrElse(i) { 0.0 },
                sunrise = daily.sunrise[i].substringAfterLast("T"),
                sunset = daily.sunset[i].substringAfterLast("T")
            )
        }

        val airQuality = aqResponse?.current?.let {
            AirQuality(
                europeanAqi = it.europeanAqi,
                usAqi = it.usAqi,
                pm25 = it.pm25,
                pm10 = it.pm10
            )
        }

        return WeatherData(
            city = city,
            currentTemp = current.temperature,
            feelsLike = current.apparentTemperature,
            tempMax = todayDaily.temperatureMax.firstOrNull() ?: current.temperature,
            tempMin = todayDaily.temperatureMin.firstOrNull() ?: current.temperature,
            condition = current.weatherCode.toWeatherCondition(),
            humidity = current.humidity,
            windSpeed = current.windSpeed,
            windDirection = current.windDirection,
            precipitation = current.precipitation,
            uvIndex = todayDaily.uvIndexMax.firstOrNull() ?: 0.0,
            sunrise = todayDaily.sunrise.firstOrNull()?.substringAfterLast("T") ?: "",
            sunset = todayDaily.sunset.firstOrNull()?.substringAfterLast("T") ?: "",
            isDay = current.isDay == 1,
            hourlyForecast = hourlyList,
            dailyForecast = dailyList,
            airQuality = airQuality
        )
    }
}
