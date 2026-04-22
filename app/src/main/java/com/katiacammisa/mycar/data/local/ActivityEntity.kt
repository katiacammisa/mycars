package com.katiacammisa.mycar.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.katiacammisa.mycar.home.data.ActivityType
import com.katiacammisa.mycar.home.data.ActivityUi

@Entity(tableName = "activities")
data class ActivityEntity(
    @PrimaryKey val id: String,
    val carName: String,
    val title: String,
    val description: String,
    val date: String,
    val mileage: String,
    val type: String,
    val createdAt: Long,
)

fun ActivityEntity.toUi(): ActivityUi {
    return ActivityUi(
        id = id,
        carName = carName,
        title = title,
        description = description,
        date = date,
        mileage = mileage,
        type = ActivityType.entries.find { it.name == type } ?: ActivityType.OTHER,
    )
}

fun ActivityUi.toEntity(createdAt: Long = System.currentTimeMillis()): ActivityEntity {
    return ActivityEntity(
        id = id,
        carName = carName,
        title = title,
        description = description,
        date = date,
        mileage = mileage,
        type = type.name,
        createdAt = createdAt,
    )
}
