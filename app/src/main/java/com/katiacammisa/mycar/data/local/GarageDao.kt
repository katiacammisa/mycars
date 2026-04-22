package com.katiacammisa.mycar.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GarageDao {

    @Query("SELECT * FROM cars ORDER BY createdAt DESC")
    fun observeCars(): Flow<List<CarEntity>>

    @Query("SELECT * FROM activities ORDER BY createdAt DESC")
    fun observeActivities(): Flow<List<ActivityEntity>>

    @Query("SELECT * FROM cars WHERE id = :carId LIMIT 1")
    fun observeCarById(carId: String): Flow<CarEntity?>

    @Query("SELECT * FROM activities WHERE carName = :carName ORDER BY createdAt ASC")
    fun observeActivitiesForCar(carName: String): Flow<List<ActivityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCar(car: CarEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: ActivityEntity)

    @Query("DELETE FROM activities WHERE id = :activityId")
    suspend fun deleteActivityById(activityId: String)

    @Query("DELETE FROM activities WHERE carName = :carName")
    suspend fun deleteActivitiesByCarName(carName: String)

    @Query("DELETE FROM cars WHERE id = :carId")
    suspend fun deleteCarById(carId: String)
}
