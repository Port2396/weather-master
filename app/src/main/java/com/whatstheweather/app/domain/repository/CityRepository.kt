package com.whatstheweather.app.domain.repository

import com.whatstheweather.app.domain.model.City
import kotlinx.coroutines.flow.Flow

interface CityRepository {
    fun getSavedCities(): Flow<List<City>>
    suspend fun saveCity(city: City)
    suspend fun deleteCity(city: City)
    suspend fun searchCities(query: String): Result<List<City>>
    suspend fun updateCurrentLocationCity(city: City)
}
