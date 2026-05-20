package com.whatstheweather.app.presentation.ui.home

import com.whatstheweather.app.domain.model.City
import com.whatstheweather.app.domain.model.WeatherData

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val weatherData: WeatherData) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
    object LocationPermissionRequired : HomeUiState()
    object NoCitySaved : HomeUiState()
}
