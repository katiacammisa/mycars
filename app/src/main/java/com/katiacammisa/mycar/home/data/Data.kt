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

enum class ActivityType(val displayName: String) {
    OIL_CHANGE("Oil Change"),
    TIRE_CHANGE("Tire Change"),
    BRAKE_SERVICE("Brake Service"),
    BATTERY_CHECK("Battery Check"),
    INSPECTION("General Inspection"),
    FUEL("Fuel"),
    GENERAL_SERVICE("General Service"),
    OTHER("Other");

    companion object {
        fun fromDisplayName(displayName: String): ActivityType {
            return entries.find { it.displayName == displayName } ?: OTHER
        }
    }
}