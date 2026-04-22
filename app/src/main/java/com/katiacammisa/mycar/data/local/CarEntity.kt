package com.katiacammisa.mycar.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.katiacammisa.mycar.home.data.CarSummaryUi

@Entity(tableName = "cars")
data class CarEntity(
    @PrimaryKey val id: String,
    val nickname: String,
    val make: String,
    val model: String,
    val year: Int,
    val plate: String,
    val mileage: String,
    val createdAt: Long,
)

fun CarEntity.toUi(): CarSummaryUi {
    return CarSummaryUi(
        id = id,
        nickname = nickname,
        make = make,
        model = model,
        year = year,
        plate = plate,
        mileage = mileage,
    )
}

fun CarSummaryUi.toEntity(createdAt: Long = System.currentTimeMillis()): CarEntity {
    return CarEntity(
        id = id,
        nickname = nickname,
        make = make,
        model = model,
        year = year,
        plate = plate,
        mileage = mileage,
        createdAt = createdAt,
    )
}
