package com.whatstheweather.app.presentation.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.whatstheweather.app.domain.model.AppSettings
import com.whatstheweather.app.domain.model.City
import com.whatstheweather.app.domain.repository.CityRepository
import com.whatstheweather.app.domain.repository.SettingsRepository
import com.whatstheweather.app.domain.usecase.GetCurrentWeatherUseCase
import com.whatstheweather.app.domain.usecase.ManageCityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getWeatherUseCase: GetCurrentWeatherUseCase,
    private val manageCityUseCase: ManageCityUseCase,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val savedCities: StateFlow<List<City>> = manageCityUseCase.getSavedCities()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val settings: StateFlow<AppSettings> = settingsRepository.getSettings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettings())

    private val _activeCityIndex = MutableStateFlow(0)
    val activeCityIndex: StateFlow<Int> = _activeCityIndex.asStateFlow()

    init {
        observeCities()
    }

    private fun observeCities() {
        viewModelScope.launch {
            savedCities.collect { cities ->
                if (cities.isEmpty()) {
                    _uiState.value = HomeUiState.NoCitySaved
                } else {
                    val city = cities.getOrElse(_activeCityIndex.value) { cities.first() }
                    loadWeather(city)
                }
            }
        }
    }

    fun loadWeather(city: City, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            getWeatherUseCase(city, forceRefresh).collect { result ->
                _uiState.value = result.fold(
                    onSuccess = { HomeUiState.Success(it) },
                    onFailure = { HomeUiState.Error(it.message ?: "Failed to load weather") }
                )
            }
        }
    }

    fun setActiveCity(index: Int) {
        _activeCityIndex.value = index
        val city = savedCities.value.getOrNull(index) ?: return
        loadWeather(city)
    }

    fun refresh() {
        val cities = savedCities.value
        val city = cities.getOrNull(_activeCityIndex.value) ?: return
        loadWeather(city, forceRefresh = true)
    }

    @SuppressLint("MissingPermission")
    fun fetchCurrentLocation() {
        viewModelScope.launch {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            val cancellationSource = CancellationTokenSource()
            runCatching {
                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    cancellationSource.token
                ).addOnSuccessListener { location ->
                    location?.let {
                        viewModelScope.launch {
                            val geocoder = Geocoder(context, Locale.getDefault())
                            @Suppress("DEPRECATION")
                            val addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                            val address = addresses?.firstOrNull()
                            val city = City(
                                name = address?.locality ?: address?.subAdminArea ?: "Current Location",
                                country = address?.countryCode ?: "",
                                latitude = it.latitude,
                                longitude = it.longitude,
                                isCurrentLocation = true
                            )
                            manageCityUseCase.saveCity(city)
                        }
                    }
                }
            }
        }
    }
}
