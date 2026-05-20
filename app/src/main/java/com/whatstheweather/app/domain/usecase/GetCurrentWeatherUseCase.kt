package com.whatstheweather.app.domain.usecase

import com.whatstheweather.app.domain.model.City
import com.whatstheweather.app.domain.model.WeatherData
import com.whatstheweather.app.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentWeatherUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {
    operator fun invoke(city: City, forceRefresh: Boolean = false): Flow<Result<WeatherData>> =
        weatherRepository.getWeather(city, forceRefresh)
}
