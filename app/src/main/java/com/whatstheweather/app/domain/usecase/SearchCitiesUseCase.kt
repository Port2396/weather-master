package com.whatstheweather.app.domain.usecase

import com.whatstheweather.app.domain.model.City
import com.whatstheweather.app.domain.repository.CityRepository
import javax.inject.Inject

class SearchCitiesUseCase @Inject constructor(
    private val cityRepository: CityRepository
) {
    suspend operator fun invoke(query: String): Result<List<City>> {
        if (query.length < 2) return Result.success(emptyList())
        return cityRepository.searchCities(query)
    }
}
