package com.katiacammisa.mycar.data.local

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideMyCarsDatabase(
        @ApplicationContext context: Context,
    ): MyCarsDatabase {
        return Room.databaseBuilder(
            context,
            MyCarsDatabase::class.java,
            "mycars.db",
        ).build()
    }

    @Provides
    @Singleton
    fun provideGarageDao(database: MyCarsDatabase): GarageDao {
        return database.garageDao()
    }
}
