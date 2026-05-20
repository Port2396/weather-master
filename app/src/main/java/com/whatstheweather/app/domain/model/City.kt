package com.whatstheweather.app.domain.model

data class City(
    val id: Long = 0,
    val name: String,
    val country: String,
    val admin1: String = "",
    val latitude: Double,
    val longitude: Double,
    val isCurrentLocation: Boolean = false,
    val orderIndex: Int = 0
)
