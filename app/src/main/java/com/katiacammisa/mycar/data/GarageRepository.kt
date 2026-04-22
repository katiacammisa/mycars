package com.katiacammisa.mycar.data

import com.katiacammisa.mycar.data.local.GarageDao
import com.katiacammisa.mycar.data.local.toEntity
import com.katiacammisa.mycar.data.local.toUi
import com.katiacammisa.mycar.home.data.ActivityUi
import com.katiacammisa.mycar.home.data.CarSummaryUi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GarageRepository @Inject constructor(
    private val garageDao: GarageDao,
) {
    val cars: Flow<List<CarSummaryUi>> = garageDao.observeCars().map { cars ->
        cars.map { it.toUi() }
    }

    val activities: Flow<List<ActivityUi>> = garageDao.observeActivities().map { activities ->
        activities.map { it.toUi() }
    }

    suspend fun addCar(car: CarSummaryUi) {
        garageDao.insertCar(car.toEntity())
    }

    suspend fun addActivity(activity: ActivityUi) {
        garageDao.insertActivity(activity.toEntity())
    }

    fun observeCar(carId: String): Flow<CarSummaryUi?> {
        return garageDao.observeCarById(carId).map { it?.toUi() }
    }

    fun observeActivitiesForCar(carName: String): Flow<List<ActivityUi>> {
        return garageDao.observeActivitiesForCar(carName).map { activities ->
            activities.map { it.toUi() }
        }
    }

    suspend fun deleteActivity(activityId: String) {
        garageDao.deleteActivityById(activityId)
    }

    suspend fun deleteCar(carId: String, carName: String) {
        garageDao.deleteActivitiesByCarName(carName)
        garageDao.deleteCarById(carId)
    }
}
