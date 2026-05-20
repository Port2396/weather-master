package com.whatstheweather.app.di

import com.whatstheweather.app.data.repository.CityRepositoryImpl
import com.whatstheweather.app.data.repository.SettingsRepositoryImpl
import com.whatstheweather.app.data.repository.WeatherRepositoryImpl
import com.whatstheweather.app.domain.repository.CityRepository
import com.whatstheweather.app.domain.repository.SettingsRepository
import com.whatstheweather.app.domain.repository.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindWeatherRepository(impl: WeatherRepositoryImpl): WeatherRepository

    @Binds @Singleton
    abstract fun bindCityRepository(impl: CityRepositoryImpl): CityRepository

    @Binds @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository
}
