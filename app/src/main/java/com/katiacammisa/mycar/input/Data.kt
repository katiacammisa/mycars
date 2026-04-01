package com.katiacammisa.mycar.input

data class CarCatalogState(
    val isLoading: Boolean = false,
    val makes: List<String> = emptyList(),
    val modelsByMake: Map<String, List<String>> = emptyMap(),
    val error: String? = null,
)