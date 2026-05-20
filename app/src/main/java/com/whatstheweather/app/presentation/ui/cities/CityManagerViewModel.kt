package com.whatstheweather.app.presentation.ui.cities

import com.whatstheweather.app.domain.model.City
import com.whatstheweather.app.domain.repository.CityRepository
import com.whatstheweather.app.domain.usecase.ManageCityUseCase
import com.whatstheweather.app.domain.usecase.SearchCitiesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CityManagerViewModel @Inject constructor(
    private val manageCityUseCase: ManageCityUseCase,
    private val searchCitiesUseCase: SearchCitiesUseCase
) : ViewModel() {

    val savedCities: StateFlow<List<City>> = manageCityUseCase.getSavedCities()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<City>>(emptyList())
    val searchResults: StateFlow<List<City>> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private var searchJob: Job? = null

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        if (query.length < 2) {
            _searchResults.value = emptyList()
            _isSearching.value = false
            return
        }
        searchJob = viewModelScope.launch {
            delay(400) // debounce
            _isSearching.value = true
            searchCitiesUseCase(query).onSuccess { results ->
                _searchResults.value = results
            }
            _isSearching.value = false
        }
    }

    fun saveCity(city: City) {
        viewModelScope.launch {
            manageCityUseCase.saveCity(city)
            _searchQuery.value = ""
            _searchResults.value = emptyList()
        }
    }

    fun deleteCity(city: City) {
        viewModelScope.launch { manageCityUseCase.deleteCity(city) }
    }
}
