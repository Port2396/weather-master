package com.whatstheweather.app.data.local.dao

import androidx.room.*
import com.whatstheweather.app.data.local.entity.CityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDao {
    @Query("SELECT * FROM cities ORDER BY isCurrentLocation DESC, orderIndex ASC")
    fun getAllCities(): Flow<List<CityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCity(city: CityEntity): Long

    @Delete
    suspend fun deleteCity(city: CityEntity)

    @Query("DELETE FROM cities WHERE isCurrentLocation = 1")
    suspend fun deleteCurrentLocationCity()

    @Query("UPDATE cities SET orderIndex = :index WHERE id = :id")
    suspend fun updateOrder(id: Long, index: Int)
}
