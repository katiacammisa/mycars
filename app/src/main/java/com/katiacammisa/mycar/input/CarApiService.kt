package com.katiacammisa.mycar.input

import retrofit2.http.GET
import retrofit2.http.Query

interface CarApiService {
    @GET("api/models/v2")
    suspend fun getModels(
        @Query("limit") limit: Int = 800,
    ): CarModelsResponse
}

data class CarModelsResponse(
    val data: List<CarModelDto>,
)

data class CarModelDto(
    val make: String,
    val name: String,
)
