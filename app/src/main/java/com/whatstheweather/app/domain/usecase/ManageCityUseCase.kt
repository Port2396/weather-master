package com.whatstheweather.app.domain.usecase

import com.whatstheweather.app.domain.model.City
import com.whatstheweather.app.domain.repository.CityRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ManageCityUseCase @Inject constructor(
    private val cityRepository: CityRepository
) {
    fun getSavedCities(): Flow<List<City>> = cityRepository.getSavedCities()
    suspend fun saveCity(city: City) = cityRepository.saveCity(city)
    suspend fun deleteCity(city: City) = cityRepository.deleteCity(city)
}
