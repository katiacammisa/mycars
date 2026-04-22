package com.katiacammisa.mycar.input

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.katiacammisa.mycar.data.GarageRepository
import com.katiacammisa.mycar.home.data.ActivityUi
import com.katiacammisa.mycar.home.data.CarSummaryUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FormsViewModel @Inject constructor(
    private val carCatalogRepository: CarCatalogRepository,
    private val garageRepository: GarageRepository,
) : ViewModel() {

    val addedCars: StateFlow<List<CarSummaryUi>> = garageRepository.cars.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    val addedActivities: StateFlow<List<ActivityUi>> = garageRepository.activities.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    private val _catalogState = MutableStateFlow(CarCatalogState(isLoading = true))
    val catalogState = _catalogState.asStateFlow()

    init {
        loadCatalog()
    }

    fun addCar(car: CarSummaryUi) {
        viewModelScope.launch {
            garageRepository.addCar(car)
        }
    }

    fun addActivity(activity: ActivityUi) {
        viewModelScope.launch {
            garageRepository.addActivity(activity)
        }
    }

    fun deleteCar(car: CarSummaryUi) {
        viewModelScope.launch {
            garageRepository.deleteCar(carId = car.id, carName = car.nickname)
        }
    }

    fun deleteActivity(activity: ActivityUi) {
        viewModelScope.launch {
            garageRepository.deleteActivity(activity.id)
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
