package com.whatstheweather.app.di

import com.whatstheweather.app.data.api.openmeteo.OpenMeteoAirQualityService
import com.whatstheweather.app.data.api.openmeteo.OpenMeteoGeocodingService
import com.whatstheweather.app.data.api.openmeteo.OpenMeteoService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    @Named("forecast")
    fun provideForecastRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(OpenMeteoService.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    @Named("airquality")
    fun provideAirQualityRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(OpenMeteoAirQualityService.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    @Named("geocoding")
    fun provideGeocodingRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(OpenMeteoGeocodingService.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideOpenMeteoService(@Named("forecast") retrofit: Retrofit): OpenMeteoService =
        retrofit.create(OpenMeteoService::class.java)

    @Provides
    @Singleton
    fun provideOpenMeteoAirQualityService(@Named("airquality") retrofit: Retrofit): OpenMeteoAirQualityService =
        retrofit.create(OpenMeteoAirQualityService::class.java)

    @Provides
    @Singleton
    fun provideGeocodingService(@Named("geocoding") retrofit: Retrofit): OpenMeteoGeocodingService =
        retrofit.create(OpenMeteoGeocodingService::class.java)
}
