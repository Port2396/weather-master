package com.whatstheweather.app.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.whatstheweather.app.domain.model.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "wtw_settings")

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val TEMP_UNIT = stringPreferencesKey("temp_unit")
        val WIND_UNIT = stringPreferencesKey("wind_unit")
        val TIME_FORMAT = stringPreferencesKey("time_format")
        val THEME = stringPreferencesKey("theme")
        val API_PROVIDER = stringPreferencesKey("api_provider")
        val OWM_API_KEY = stringPreferencesKey("owm_api_key")
        val WEATHER_API_KEY = stringPreferencesKey("weather_api_key")
        val TOMORROW_API_KEY = stringPreferencesKey("tomorrow_api_key")
        val CACHE_MINUTES = intPreferencesKey("cache_minutes")
        val NOTIFICATIONS = booleanPreferencesKey("notifications")
    }

    val settings: Flow<AppSettings> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs ->
            AppSettings(
                temperatureUnit = TemperatureUnit.valueOf(prefs[Keys.TEMP_UNIT] ?: TemperatureUnit.CELSIUS.name),
                windSpeedUnit = WindSpeedUnit.valueOf(prefs[Keys.WIND_UNIT] ?: WindSpeedUnit.KMH.name),
                timeFormat = TimeFormat.valueOf(prefs[Keys.TIME_FORMAT] ?: TimeFormat.HOUR_12.name),
                theme = AppTheme.valueOf(prefs[Keys.THEME] ?: AppTheme.SYSTEM.name),
                apiProvider = WeatherApiProvider.valueOf(prefs[Keys.API_PROVIDER] ?: WeatherApiProvider.OPEN_METEO.name),
                openWeatherMapApiKey = prefs[Keys.OWM_API_KEY] ?: "",
                weatherApiKey = prefs[Keys.WEATHER_API_KEY] ?: "",
                tomorrowApiKey = prefs[Keys.TOMORROW_API_KEY] ?: "",
                cacheMinutes = prefs[Keys.CACHE_MINUTES] ?: 30,
                notificationsEnabled = prefs[Keys.NOTIFICATIONS] ?: false
            )
        }

    suspend fun updateSettings(settings: AppSettings) {
        context.dataStore.edit { prefs ->
            prefs[Keys.TEMP_UNIT] = settings.temperatureUnit.name
            prefs[Keys.WIND_UNIT] = settings.windSpeedUnit.name
            prefs[Keys.TIME_FORMAT] = settings.timeFormat.name
            prefs[Keys.THEME] = settings.theme.name
            prefs[Keys.API_PROVIDER] = settings.apiProvider.name
            prefs[Keys.OWM_API_KEY] = settings.openWeatherMapApiKey
            prefs[Keys.WEATHER_API_KEY] = settings.weatherApiKey
            prefs[Keys.TOMORROW_API_KEY] = settings.tomorrowApiKey
            prefs[Keys.CACHE_MINUTES] = settings.cacheMinutes
            prefs[Keys.NOTIFICATIONS] = settings.notificationsEnabled
        }
    }
}
