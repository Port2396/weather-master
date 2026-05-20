package com.whatstheweather.app.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whatstheweather.app.domain.model.AppTheme
import com.whatstheweather.app.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * Top-level ViewModel that exposes only the theme preference, so the root
 * composable can re-theme the whole app when the user changes it in Settings.
 */
@HiltViewModel
class AppThemeViewModel @Inject constructor(
    settingsRepository: SettingsRepository
) : ViewModel() {

    val theme: StateFlow<AppTheme> = settingsRepository.getSettings()
        .map { it.theme }
        .stateIn(viewModelScope, SharingStarted.Eagerly, AppTheme.SYSTEM)
}
