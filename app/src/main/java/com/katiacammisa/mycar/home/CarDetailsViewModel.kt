package com.katiacammisa.mycar.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.katiacammisa.mycar.data.GarageRepository
import com.katiacammisa.mycar.home.data.ActivityUi
import com.katiacammisa.mycar.home.data.CarSummaryUi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class CarDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val garageRepository: GarageRepository,
) : ViewModel() {

    private val carId: String = savedStateHandle.get<String>("carId").orEmpty()

    val car: StateFlow<CarSummaryUi?> = garageRepository.observeCar(carId).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    private val _activities = MutableStateFlow<List<ActivityUi>>(emptyList())
    val activities: StateFlow<List<ActivityUi>> = _activities.asStateFlow()

    init {
        viewModelScope.launch {
            car.collectLatest { selectedCar ->
                if (selectedCar == null) {
                    _activities.value = emptyList()
                } else {
                    garageRepository.observeActivitiesForCar(selectedCar.nickname).collect { carActivities ->
                        _activities.value = carActivities
                    }
                }
            }
        }
    }

    fun deleteActivity(activity: ActivityUi) {
        viewModelScope.launch {
            garageRepository.deleteActivity(activity.id)
        }
    }

    fun deleteCar(onDeleted: () -> Unit) {
        val selectedCar = car.value ?: return
        viewModelScope.launch {
            garageRepository.deleteCar(carId = selectedCar.id, carName = selectedCar.nickname)
            onDeleted()
        }
    }
}
