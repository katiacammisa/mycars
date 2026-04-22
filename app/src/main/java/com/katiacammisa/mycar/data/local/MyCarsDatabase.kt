package com.katiacammisa.mycar.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [CarEntity::class, ActivityEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class MyCarsDatabase : RoomDatabase() {
    abstract fun garageDao(): GarageDao
}
