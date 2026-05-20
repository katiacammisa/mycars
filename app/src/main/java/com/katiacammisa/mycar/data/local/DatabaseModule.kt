package com.katiacammisa.mycar.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private val migration1To2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE cars ADD COLUMN photoPath TEXT")
        }
    }

    @Provides
    @Singleton
    fun provideMyCarsDatabase(
        @ApplicationContext context: Context,
    ): MyCarsDatabase {
        return Room.databaseBuilder(
            context,
            MyCarsDatabase::class.java,
            "mycars.db",
        ).addMigrations(migration1To2)
            .build()
    }

    @Provides
    @Singleton
    fun provideGarageDao(database: MyCarsDatabase): GarageDao {
        return database.garageDao()
    }
}
