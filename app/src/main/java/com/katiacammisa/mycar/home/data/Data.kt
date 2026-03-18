package com.katiacammisa.mycar.home.data

data class CarSummaryUi(
    val id: String,
    val nickname: String,
    val make: String,
    val model: String,
    val year: Int,
    val plate: String,
    val mileage: String,
)

data class ActivityUi(
    val id: String,
    val carName: String,
    val title: String,
    val description: String,
    val date: String,
    val mileage: String,
    val type: ActivityType,
)

enum class ActivityType {
    OIL_CHANGE,
    TIRE_CHANGE,
    INSPECTION,
    FUEL,
    GENERAL_SERVICE,
}