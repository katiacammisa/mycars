package com.katiacammisa.mycar.input

import javax.inject.Inject

class CarCatalogRepository @Inject constructor(
    private val carApiService: CarApiService,
) {
    suspend fun getModelsByMake(): Map<String, List<String>> {
        val response = carApiService.getModels()
        return response.data
            .groupBy { it.make.trim() }
            .mapValues { (_, models) ->
                models.map { it.name.trim() }
                    .distinct()
                    .sorted()
            }
            .filterKeys { it.isNotEmpty() }
            .toSortedMap()
    }
}
