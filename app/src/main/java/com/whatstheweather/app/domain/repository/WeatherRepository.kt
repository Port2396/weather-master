package com.whatstheweather.app.domain.repository

import com.whatstheweather.app.domain.model.City
import com.whatstheweather.app.domain.model.WeatherData
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    fun getWeather(city: City, forceRefresh: Boolean = false): Flow<Result<WeatherData>>
}
