package com.whatstheweather.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cities")
data class CityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val country: String,
    val admin1: String = "",
    val latitude: Double,
    val longitude: Double,
    val isCurrentLocation: Boolean = false,
    val orderIndex: Int = 0
)
