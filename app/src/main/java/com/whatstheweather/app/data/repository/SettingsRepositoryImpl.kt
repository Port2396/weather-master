package com.whatstheweather.app.data.repository

import com.whatstheweather.app.data.datastore.SettingsDataStore
import com.whatstheweather.app.domain.model.AppSettings
import com.whatstheweather.app.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : SettingsRepository {
    override fun getSettings(): Flow<AppSettings> = settingsDataStore.settings
    override suspend fun updateSettings(settings: AppSettings) = settingsDataStore.updateSettings(settings)
}
