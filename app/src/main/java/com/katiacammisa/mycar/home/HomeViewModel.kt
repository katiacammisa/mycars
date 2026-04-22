package com.katiacammisa.mycar.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.katiacammisa.mycar.data.GarageRepository
import com.katiacammisa.mycar.home.data.ActivityUi
import com.katiacammisa.mycar.home.data.CarSummaryUi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val garageRepository: GarageRepository,
) : ViewModel() {

    val cars: StateFlow<List<CarSummaryUi>> = garageRepository.cars.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    val activities: StateFlow<List<ActivityUi>> = garageRepository.activities.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    fun deleteActivity(activityId: String) {
        viewModelScope.launch {
            garageRepository.deleteActivity(activityId)
        }
    }
}
