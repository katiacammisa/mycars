package com.katiacammisa.mycar.input

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.katiacammisa.mycar.home.data.ActivityUi
import com.katiacammisa.mycar.home.data.CarSummaryUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FormsViewModel @Inject constructor(
    private val carCatalogRepository: CarCatalogRepository,
) : ViewModel() {

    private var _addedCars = MutableStateFlow(listOf<CarSummaryUi>())
    val addedCars = _addedCars.asStateFlow()

    private var _addedActivities = MutableStateFlow(listOf<ActivityUi>())
    val addedActivities = _addedActivities.asStateFlow()

    private val _catalogState = MutableStateFlow(CarCatalogState(isLoading = true))
    val catalogState = _catalogState.asStateFlow()

    init {
        loadCatalog()
    }

    fun addCar(car: CarSummaryUi) {
        viewModelScope.launch {
            _addedCars.emit(_addedCars.value + car)
        }
    }

    fun addActivity(activity: ActivityUi) {
        viewModelScope.launch {
            _addedActivities.emit(_addedActivities.value + activity)
        }
    }

    fun loadCatalog() {
        viewModelScope.launch {
            _catalogState.value = _catalogState.value.copy(isLoading = true, error = null)
            runCatching { carCatalogRepository.getModelsByMake() }
                .onSuccess { modelsByMake ->
                    _catalogState.value = CarCatalogState(
                        isLoading = false,
                        makes = modelsByMake.keys.toList(),
                        modelsByMake = modelsByMake,
                        error = null,
                    )
                }
                .onFailure {
                    _catalogState.value = _catalogState.value.copy(
                        isLoading = false,
                        error = "Could not load makes and models right now.",
                    )
                }
        }
    }
}
