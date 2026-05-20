package com.whatstheweather.app.data.repository

import com.whatstheweather.app.data.api.openmeteo.*
import com.whatstheweather.app.data.local.dao.CityDao
import com.whatstheweather.app.data.local.entity.CityEntity
import com.whatstheweather.app.domain.model.City
import com.whatstheweather.app.domain.repository.CityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CityRepositoryImpl @Inject constructor(
    private val cityDao: CityDao,
    private val geocodingService: OpenMeteoGeocodingService
) : CityRepository {

    override fun getSavedCities(): Flow<List<City>> =
        cityDao.getAllCities().map { entities -> entities.map { it.toDomain() } }

    override suspend fun saveCity(city: City) {
        cityDao.insertCity(city.toEntity())
    }

    override suspend fun deleteCity(city: City) {
        cityDao.deleteCity(city.toEntity())
    }

    override suspend fun searchCities(query: String): Result<List<City>> = runCatching {
        val response = geocodingService.searchCities(query)
        response.results?.map { it.toDomain() } ?: emptyList()
    }

    override suspend fun updateCurrentLocationCity(city: City) {
        cityDao.deleteCurrentLocationCity()
        cityDao.insertCity(city.toEntity())
    }

    private fun CityEntity.toDomain() = City(
        id = id, name = name, country = country, admin1 = admin1,
        latitude = latitude, longitude = longitude,
        isCurrentLocation = isCurrentLocation, orderIndex = orderIndex
    )

    private fun City.toEntity() = CityEntity(
        id = id, name = name, country = country, admin1 = admin1,
        latitude = latitude, longitude = longitude,
        isCurrentLocation = isCurrentLocation, orderIndex = orderIndex
    )

    private fun GeocodingResultDto.toDomain() = City(
        id = id, name = name, country = country,
        admin1 = admin1 ?: "",
        latitude = latitude, longitude = longitude
    )
}
